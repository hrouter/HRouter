package com.lq.compiler.degrade

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lq.compiler.ProcessorPart
import com.lq.compiler.util.Const
import com.lq.compiler.util.MetaInfUtil
import com.lq.compiler.util.capitalizeFirst
import com.lq.compiler.util.getModuleNameCapitalize
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec

internal class RouteDegradePart(
    environment: SymbolProcessorEnvironment,
) : ProcessorPart {
    val moduleName = getModuleNameCapitalize(environment)
    val codeGenerator = environment.codeGenerator

    override fun process(resolver: Resolver) {
        val degrades = mutableListOf<String>()
        resolver.getSymbolsWithAnnotation(Const.DegradeQualifiedName).filterIsInstance<KSClassDeclaration>().forEach {
            val symbol =
                it.annotations.first { annotation ->
                    annotation.shortName.asString() == Const.DEGRADE_SHORT_NAME
                }
            val classPath = it.qualifiedName?.asString() ?: return@forEach
            var priority = 0
            var path = ""
            symbol.arguments.forEach { arg ->
                when (arg.name?.asString()) {
                    "path" -> path = arg.value as String
                    "priority" -> priority = arg.value as Int
                }
            }
            degrades += "DegradeMeta(\"$classPath\",$priority,\"$path\")"
        }
        if (degrades.isNotEmpty()) {
            generateRouteDegradeRegister(degrades)
        }
    }

    fun generateRouteDegradeRegister(degrades: List<String>) {
        val className = "RouteDegrade${moduleName.capitalizeFirst()}"
        val funSpec =
            FunSpec
                .builder("register")
                .addParameter("degrades", Const.MutableListClassName.parameterizedBy(Const.DegradeMetaClassName))
                .addModifiers(KModifier.OVERRIDE)

        for (degrade in degrades) {
            funSpec.addStatement("degrades.add($degrade)")
        }
        val typeSpec = TypeSpec.objectBuilder(className).addFunction(funSpec.build()).addSuperinterface(Const.DegradeRegisterClassName)
        val fileSpec = FileSpec.builder(Const.H_ROUTER_PACKAGE, className).addType(typeSpec.build()).build()

        val file = codeGenerator.createNewFile(Dependencies(false), Const.H_ROUTER_PACKAGE, className)

        file.bufferedWriter().use {
            fileSpec.writeTo(it)
        }
        MetaInfUtil.writeMetaInf(codeGenerator, "${Const.H_ROUTER_PACKAGE}.$className", moduleName, Const.DEGRADE_CONTRACT)
    }
}
