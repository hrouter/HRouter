package com.lq.hrouter_plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ContractTask : DefaultTask(){

   init {
       group = "TestGradle"
       description = "clean up the locked class.jar"
       dependsOn("kspDebugKotlin")
   }

    @TaskAction
    fun runAction(){
        val routeClasses = findUtil("route")
        generateHRouteIndex(routeClasses)

        val deeplinkClasses = findUtil("deeplink")
        generateDeepLinkIndex(deeplinkClasses)

        generateInterceptorIndex(findUtil("interceptor"))
        generateDegradeIndex(findUtil("degrade"))
    }


    private fun findUtil(path:String): MutableSet<String>{
        val rootClassNames = mutableSetOf<String>()
        project.rootProject.subprojects.forEach {
            val metaInfDir = File(it.buildDir, "generated/ksp/debug/resources/META-INF/$path")
            if(metaInfDir.exists()){
                metaInfDir.listFiles()?.forEach { file ->
//                    println("HRouterIndex file:${file.name}")
                    val lines = file.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                    rootClassNames.addAll(lines)
                }
            }
        }

        return rootClassNames
    }

    private fun generateHRouteIndex(rootClassNames: MutableSet<String>){
        val outputDir = File(project.buildDir, "generated/route")
        val outputFile = File(outputDir, "HRouterIndex.kt")

        // 确保父目录存在
        outputFile.parentFile.mkdirs()

        // 构建 HRouterIndex 类内容
        val fileContent = buildString {
            appendLine("package com.lq.router")
            appendLine()
            appendLine("import com.lq.lib_annotation.route.IRouteRoot")
            appendLine()
            appendLine("object HRouterIndex {")
            appendLine("    val roots: List<IRouteRoot> by lazy {")
            appendLine("        listOf(")
            rootClassNames.forEach { className ->
                appendLine("            $className(),")
            }
            appendLine("        )")
            appendLine("    }")
            appendLine("}")
        }

        outputFile.writeText(fileContent)

    }

    private fun generateInterceptorIndex(classNames: MutableSet<String>){
        val outputDir = File(project.buildDir, "generated/interceptor")
        val outputFile = File(outputDir, "InterceptorIndex.kt")

        outputFile.parentFile.mkdirs()

        val fileContent = buildString {
            appendLine("package com.lq.router")
            appendLine()
            appendLine("import com.lq.lib_annotation.interceptor.IInterceptorRegister")
            appendLine()
            appendLine("object InterceptorIndex {")
            appendLine("    val roots: List<IInterceptorRegister> by lazy {")
            appendLine("        listOf(")
            classNames.forEach { className ->
                appendLine("            $className,")
            }
            appendLine("        )")
            appendLine("    }")
            appendLine("}")
        }

        outputFile.writeText(fileContent)

    }

    private fun generateDegradeIndex(classNames: MutableSet<String>){
        val outputDir = File(project.buildDir, "generated/degrade")
        val outputFile = File(outputDir, "DegradeIndex.kt")

        outputFile.parentFile.mkdirs()

        val fileContent = buildString {
            appendLine("package com.lq.router")
            appendLine()
            appendLine("import com.lq.lib_annotation.degrade.IDegradeRegister")
            appendLine()
            appendLine("object DegradeIndex {")
            appendLine("    val roots: List<IDegradeRegister> by lazy {")
            appendLine("        listOf(")
            classNames.forEach { className ->
                appendLine("            $className,")
            }
            appendLine("        )")
            appendLine("    }")
            appendLine("}")
        }

        outputFile.writeText(fileContent)

    }

    private fun generateDeepLinkIndex(classNames: MutableSet<String>){
        val outputDir = File(project.buildDir, "generated/deeplink")
        val outputFile = File(outputDir, "DeepLinkIndex.kt")

        outputFile.parentFile.mkdirs()

        val fileContent = buildString {
            appendLine("package com.lq.router")
            appendLine()
            appendLine("import com.lq.lib_annotation.deeplink.IDeepLinkRegister")
            appendLine()
            appendLine("object DeepLinkIndex {")
            appendLine("    val roots: List<IDeepLinkRegister> by lazy {")
            appendLine("        listOf(")
            classNames.forEach { className ->
                appendLine("            $className,")
            }
            appendLine("        )")
            appendLine("    }")
            appendLine("}")
        }

        outputFile.writeText(fileContent)

    }

}