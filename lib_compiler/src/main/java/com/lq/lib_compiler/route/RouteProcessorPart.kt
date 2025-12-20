package com.lq.lib_compiler.route

import com.lq.lib_compiler.ProcessorPart
import com.lq.lib_compiler.util.Const
import com.lq.lib_compiler.util.MetaInfUtil
import com.lq.lib_compiler.util.RouteDeepLinkUtil
import com.lq.lib_compiler.util.getGroupName
import com.lq.lib_compiler.util.getRootName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import kotlin.collections.iterator

internal class RouteSymbolProcessorPart(environment: SymbolProcessorEnvironment):
    ProcessorPart {
    private val logger : KSPLogger = environment.logger
    private val codeGenerator: CodeGenerator = environment.codeGenerator
    private val moduleName = environment.options["moduleName"]?:"default"
    override fun process(resolver: Resolver) {
        if (resolver.getNewFiles().none()) return
        val routeMap = mutableMapOf<String, MutableMap<String, String>>()
        resolver.getSymbolsWithAnnotation(Const.RouteQualifiedName)
            .filterIsInstance<KSClassDeclaration>()
            .forEach {
                val symbol = it.annotations.first { annotation ->
                    annotation.shortName.asString() == Const.ROUTE_SHORT_NAME
                }
                val path = symbol.arguments.firstOrNull()?.value as? String ?: return@forEach
                val classInfo = it.qualifiedName?.asString() ?: return@forEach
                val group = path.split("/").getOrNull(1) ?: return@forEach

                //相同path 编译期应做提示
                if(routeMap.containsKey(group) && routeMap[group]?.contains(path) == true){
                    logger.error(
                        "HRouter ERROR: Route '$group' '$path' is duplicated.\n" +
                                " - First: ${routeMap[group]?.get(path)} \n" +
                                " - Second: $classInfo"
                    )
                    return
                }
                routeMap.getOrPut(group) { mutableMapOf() }[path] = classInfo

                RouteDeepLinkUtil.classToPath(classInfo,path)
            }
        routeMap.forEach {
            logger.warn("Route Map Info :$it")
        }
        if(routeMap.isNotEmpty()){
            generateGroup(routeMap)
            generateRoot(routeMap)
        }
    }

    private fun generateRoot(routeMap:Map<String,Map<String, String>>){
        val rootClassName = getRootName(moduleName)
        val rootInterface = Const.RouteRootClassName
        val function = FunSpec.builder("loadInto").addModifiers(KModifier.OVERRIDE).addParameter("rootMap",
            Const.MutableMapClassName.parameterizedBy(Const.StringClassName,Const.StringClassName))

        for (group in routeMap.keys){
            val className = "${Const.H_ROUTER_PACKAGE}.${getGroupName(group)}"
            function.addStatement("rootMap[%S] = %S",group,className)
        }

        val classInfo = TypeSpec.classBuilder(rootClassName).addSuperinterface(rootInterface).addFunction(function.build()).build()

        val fileSpec = FileSpec.builder(Const.H_ROUTER_PACKAGE, rootClassName)
            .addType(classInfo).build()

        val file = codeGenerator.createNewFile(Dependencies(false),Const.H_ROUTER_PACKAGE,rootClassName)
        file.bufferedWriter().use { writer ->
            fileSpec.writeTo(writer)
        }

        MetaInfUtil.writeMetaInf(codeGenerator,"${Const.H_ROUTER_PACKAGE}.$rootClassName",moduleName,Const.ROUTE_CONTRACT)
    }

    fun generateGroup(groupMap: Map<String, Map<String, String>>) {
        for ((groupName,pathMap) in groupMap){
            val className = getGroupName(groupName)
            val routeMetaClass = Const.RouteMetaClassName
            val groupInterface = Const.RouteGroupClassName

            val function = FunSpec.builder("loadInto").addModifiers(KModifier.OVERRIDE)
                .addParameter("groupMap",Const.MutableMapClassName.parameterizedBy(
                    Const.StringClassName,routeMetaClass))
                .addParameter("path",Const.StringClassName)
            val safeLoadPathMember = MemberName(
                Const.SAFE_LOAD_PATH,
                Const.SAFE_LOAD_PATH_NAME
            )
            for ((path,classInfo) in pathMap){
                val destinationClassName = ClassName.bestGuess(classInfo)
                val routeMetaInstance = CodeBlock.of(
                    "%T(%S, %T::class.java, %S)",
                    routeMetaClass, path, destinationClassName, groupName
                )
                function.addStatement("%M(groupMap,path,%L)",safeLoadPathMember,routeMetaInstance)
            }
            val fileSpec = FileSpec.builder(Const.H_ROUTER_PACKAGE, className)
                .addType(TypeSpec.classBuilder(className).addSuperinterface(groupInterface).addFunction(function.build()).build()).build()

            val file = codeGenerator.createNewFile(
                Dependencies(aggregating = true),
                Const.H_ROUTER_PACKAGE,
                getGroupName(groupName)
            )
            file.bufferedWriter().use { writer ->
                fileSpec.writeTo(writer)
            }
        }
    }


}



