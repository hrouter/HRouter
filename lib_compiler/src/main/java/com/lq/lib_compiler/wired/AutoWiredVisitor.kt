package com.lq.lib_compiler.wired

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.lq.lib_compiler.util.Const
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName

internal class AutoWiredVisitor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val className = classDeclaration.simpleName.asString()
        val packageName = classDeclaration.packageName.asString()

        logger.warn("className: $className   packageName: $packageName")

        val fileSpec = FileSpec.builder(packageName, "${className}AutoWired")
        val classSpec = TypeSpec.classBuilder("${className}AutoWired")

        val adapterClassName = Const.AutoWiredTypeAdapterClassName

        val funcSpec = FunSpec.builder("inject")
            .addParameter("target", ClassName(packageName, className))
            .addParameter("bundle", ClassName("android.os", "Bundle").copy(nullable = true))

        classDeclaration.getAllProperties().forEach { property ->
            val symbol = property.annotations.find { it.shortName.asString() == "AutoWired" } ?: return@forEach
            val required = symbol.arguments.first().value as Boolean
            val injectedName = property.simpleName.asString()

            val type = property.type.resolve()
            val isLateinit = property.modifiers.contains(Modifier.LATEINIT)
            val isNullable = property.type.resolve().isMarkedNullable

            logger.warn("inject: $injectedName is type:$type   nullable: $isNullable   lateinit: $isLateinit")

            val typeName = type.toTypeName()
            val adapterType = typeName.copy(nullable = false) // adapter 必须非空类型

            val code = when {
                isLateinit -> {
                    // lateinit：强制非空，注入失败直接抛异常
                    "target.%L = %T.getAdapter(%T::class)?.invoke(bundle, %S) as? %T ?: throw IllegalStateException(\"lateinit field '%L' cannot be null\")"
                }
                isNullable -> {
                    // 可空：允许 null
                    "target.%L = %T.getAdapter(%T::class)?.invoke(bundle, %S) as? %T"
                }
                else -> {
                    // 非nullable非lateinit：保留默认值
                    "target.%L = %T.getAdapter(%T::class)?.invoke(bundle, %S) as? %T ?: target.%L"
                }
            }

            when {
                isLateinit -> funcSpec.addStatement(code, injectedName, adapterClassName, adapterType, injectedName, typeName, injectedName)
                isNullable -> funcSpec.addStatement(code, injectedName, adapterClassName, adapterType, injectedName, typeName)
                else -> funcSpec.addStatement(code, injectedName, adapterClassName, adapterType, injectedName, typeName, injectedName)
            }
        }

        classSpec.addFunction(funcSpec.build())

        val info = fileSpec.addType(classSpec.build()).build()
        val file = codeGenerator.createNewFile(
            Dependencies(true),
            packageName,
            "${className}AutoWired"
        )
        file.bufferedWriter().use { info.writeTo(it) }

        logger.warn("AutoWiredVisitor completed for $className")
    }

}