package com.lq.compiler.interceptor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lq.compiler.ProcessorPart
import com.lq.compiler.util.Const
import com.lq.compiler.util.Const.H_ROUTER_PACKAGE
import com.lq.compiler.util.MetaInfUtil
import com.lq.compiler.util.capitalizeFirst
import com.lq.compiler.util.getModuleNameCapitalize
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import kotlin.io.bufferedWriter

internal class RouteInterceptorPart(
    environment: SymbolProcessorEnvironment,
) : ProcessorPart {
    private val codeGenerator: CodeGenerator = environment.codeGenerator
    private val moduleName = getModuleNameCapitalize(environment)

    override fun process(resolver: Resolver) {
        val routeInterceptors = mutableListOf<String>()
        resolver
            .getSymbolsWithAnnotation(Const.InterceptorQualifiedName)
            .filterIsInstance<KSClassDeclaration>()
            .forEach {
                val symbol =
                    it.annotations.first { annotation ->
                        annotation.shortName.asString() == Const.INTERCEPTOR_SHORT_NAME
                    }

                val className = it.qualifiedName?.asString() ?: return@forEach
                var path = ""
                var priority = 0
                symbol.arguments.forEach { arg ->
                    when (arg.name?.asString()) {
                        "path" -> path = arg.value as String
                        "priority" -> priority = arg.value as Int
                    }
                }
//                logger.warn("Route Interceptor Class Path :$className  Priority:$priority")
                routeInterceptors += "InterceptorMeta(\"$className\",$priority,\"$path\")"
            }
        if (routeInterceptors.isNotEmpty()) {
            generateIntercept(routeInterceptors)
        }
    }

    fun generateIntercept(interceptors: List<String>) {
        val className = "InterceptorRegister${moduleName.capitalizeFirst()}"
        val functionBuilder =
            FunSpec
                .builder("register")
                .addParameter(
                    "interceptors",
                    Const.MutableListClassName
                        .parameterizedBy(Const.InterceptorMetaClassName),
                ).addModifiers(KModifier.OVERRIDE)
        interceptors.forEach {
            functionBuilder.addStatement("interceptors.add($it)")
        }

        val typeBuilder =
            TypeSpec
                .objectBuilder(className)
                .addSuperinterface(Const.InterceptorRegisterClassName)
                .addFunction(functionBuilder.build())
        val fileSpec = FileSpec.builder(H_ROUTER_PACKAGE, className).addType(typeBuilder.build()).build()
        val file = codeGenerator.createNewFile(Dependencies(false), H_ROUTER_PACKAGE, className)
        file.bufferedWriter().use { writer ->
            fileSpec.writeTo(writer)
        }
        MetaInfUtil.writeMetaInf(codeGenerator, "$H_ROUTER_PACKAGE.$className", moduleName, Const.INTERCEPTOR_CONTRACT)
    }
}
