package com.lq.compiler.deeplink

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lq.compiler.ProcessorPart
import com.lq.compiler.util.Const
import com.lq.compiler.util.MetaInfUtil
import com.lq.compiler.util.RouteDeepLinkUtil
import com.lq.compiler.util.getModuleNameCapitalize
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import kotlin.sequences.forEach

internal class RouteDeepLinkPart(
    private val environment: SymbolProcessorEnvironment,
) : ProcessorPart {
    private val moduleName = getModuleNameCapitalize(environment)
    private val codeGenerator = environment.codeGenerator

    override fun process(resolver: Resolver) {
        if (resolver.getNewFiles().none()) return
        resolver
            .getSymbolsWithAnnotation(Const.DeepLinkQualifiedName)
            .filterIsInstance<KSClassDeclaration>()
            .forEach {
                val symbol =
                    it.annotations.first { annotation ->
                        annotation.shortName.asString() == Const.DEEPLINK_SHORT_NAME
                    }
                val classInfo = it.qualifiedName?.asString() ?: return@forEach

                val schemes = getAnnotationArgumentArray(symbol, "schemes")
                val hosts = getAnnotationArgumentArray(symbol, "hosts")
                val paths = getAnnotationArgumentArray(symbol, "paths")

                val linkList = RouteDeepLinkUtil.generateDeepLinkUrls(schemes, hosts, paths)
                RouteDeepLinkUtil.classToLinks(classInfo, linkList.toTypedArray())
            }

        if (RouteDeepLinkUtil.linkIsNotEmpty()) {
            generateLinkTable()
        }
    }

    fun generateLinkTable() {
        val className = "DeepLinkRegister$moduleName"
        val data = RouteDeepLinkUtil.contractPathToLink()
        val function =
            FunSpec
                .builder("register")
                .addParameter("map", Const.MutableMapClassName.parameterizedBy(Const.StringClassName, Const.StringClassName))
                .addModifiers(KModifier.OVERRIDE)
        data.forEach {
            it.value.forEach { link ->
                function.addStatement("map[%S] = %S", link, it.key)
            }
        }
        val typeSpec = TypeSpec.objectBuilder(className).addSuperinterface(Const.DeepLinkRegisterClassName).addFunction(function.build())
        val fileSpe = FileSpec.builder(Const.H_ROUTER_PACKAGE, className).addType(typeSpec.build()).build()
        val file =
            codeGenerator.createNewFile(
                Dependencies(aggregating = true),
                Const.H_ROUTER_PACKAGE,
                className,
            )
        file.bufferedWriter().use { writer ->
            fileSpe.writeTo(writer)
        }
        MetaInfUtil.writeMetaInf(codeGenerator, "${Const.H_ROUTER_PACKAGE}.$className", moduleName, Const.DEEPLINK_CONTRACT)
        RouteDeepLinkUtil.clear()
    }

    private fun getAnnotationArgumentArray(
        annotation: KSAnnotation,
        argumentName: String,
    ): List<String> =
        annotation.arguments
            .firstOrNull { it.name?.asString() == argumentName }
            ?.let { arg ->
                when (val value = arg.value) {
                    is List<*> -> value.filterIsInstance<String>()
                    is Array<*> -> value.filterIsInstance<String>().toList()
                    else -> emptyList()
                }
            } ?: emptyList()
}
