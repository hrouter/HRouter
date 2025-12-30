package build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class HRouterBuild : DefaultTask(){

   init {
       group = "TestGradle"
       description = "clean up the locked class.jar"
       dependsOn("kspDebugKotlin")
   }

    @TaskAction
    fun runAction(){
        generateInterceptorIndex(findUtil("interceptor"))
        generateDegradeIndex(findUtil("degrade"))
        generateHRouteIndex(findUtil("route"))
        generateDeepLinkIndex(findUtil("deeplink"))
    }


    private fun findUtil(path:String): MutableSet<String>{
        val rootClassNames = mutableSetOf<String>()
        project.rootProject.subprojects.forEach {
            val metaInfDir: File = it.layout.buildDirectory
                .dir("generated/ksp/debug/resources/META-INF/$path")
                .get()          // 获取 Directory 对象
                .asFile
            if(metaInfDir.exists()){
                metaInfDir.listFiles()?.forEach { file ->
                    println("HRouterIndex file:${file.name}")
                    val lines = file.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                    rootClassNames.addAll(lines)
                }
            }
        }


        println("✅ 生成成功，包含 ${rootClassNames.size} 个  类")
        return rootClassNames
    }

    private fun generateHRouteIndex(rootClassNames: MutableSet<String>){
        val outputDir: File = project.layout.buildDirectory
            .dir("generated/route")
            .get()          // 获取 Directory 对象
            .asFile
//        val outputDir = File(project.buildDir, "generated/route")
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

        println("✅ HRouterIndex.kt 已生成，包含 ${rootClassNames.size} 个 Root 类")
        println("路径: ${outputFile.absolutePath}")
    }

    private fun generateInterceptorIndex(classNames: MutableSet<String>){
        val outputDir: File = project.layout.buildDirectory
            .dir("generated/interceptor")
            .get()          // 获取 Directory 对象
            .asFile
//        val outputDir = File(project.buildDir, "generated/interceptor")
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

        println("✅ InterceptorIndex.kt 已生成，包含 ${classNames.size} 个 Interceptor 类")
        println("路径: ${outputFile.absolutePath}")
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

        println("✅ HRouterIndex.kt 已生成，包含 ${classNames.size} 个 Degrade 类")
        println("路径: ${outputFile.absolutePath}")
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

        println("✅ DeepLinkIndex.kt 已生成，包含 ${classNames.size} 个 DeepLink 类")
        println("路径: ${outputFile.absolutePath}")
    }

}