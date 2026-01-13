package com.lq.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ContractTask : DefaultTask() {
    init {
        group = "TestGradle"
        description = "clean up the locked class.jar"
    }

    @get:Internal
    var vName: String = ""

    @get:OutputDirectory
    abstract val customOutputDir: DirectoryProperty

    @TaskAction
    fun runAction() {
        println("vName:$vName")

        val outputDirFile = customOutputDir.get().asFile
        println("customOutputDir:${outputDirFile.path}")

        val routeClasses = findUtil("route")
        val deeplinkClasses = findUtil("deeplink")
        val interceptorClasses = findUtil("interceptor")
        val degradeClasses = findUtil("degrade")

        // 2. 统一生成：全部使用 customOutputDir
        generateFile(routeClasses, "HRouterIndex", "route", "IRouteRoot", outputDirFile)
        generateFile(deeplinkClasses, "DeepLinkIndex", "deeplink", "IDeepLinkRegister", outputDirFile)
        generateFile(interceptorClasses, "InterceptorIndex", "interceptor", "IInterceptorRegister", outputDirFile)
        generateFile(degradeClasses, "DegradeIndex", "degrade", "IDegradeRegister", outputDirFile)
    }

    private fun findUtil(path: String): MutableSet<String> {
        val rootClassNames = mutableSetOf<String>()
        project.rootProject.subprojects.forEach {
            val metaInfDir = File(it.buildDir, "generated/ksp/$vName/resources/META-INF/$path")
            if (metaInfDir.exists()) {
                metaInfDir.listFiles()?.forEach { file ->
//                    println("HRouterIndex file:${file.name}")
                    val lines = file.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                    rootClassNames.addAll(lines)
                }
            }
        }

        return rootClassNames
    }

    private fun generateFile(
        classNames: MutableSet<String>,
        fileName: String,
        tag: String,
        interfaceName: String,
        outputDir: File,
    ) {
        val packageFolder = File(outputDir, "com/lq/router")
        if (!packageFolder.exists()) packageFolder.mkdirs()

        val outputFile = File(packageFolder, "$fileName.kt")

        val fileContent =
            buildString {
                appendLine("package com.lq.router")
                appendLine()
                appendLine("import com.lq.annotation.$tag.$interfaceName")
                // 为了安全，建议根据接口名引入对应的 import，或者直接写全路径
                appendLine()
                appendLine("object $fileName {")
                appendLine("    val roots: List<Any> by lazy {") // 临时用 Any 避开 import 复杂的包名
                appendLine("        listOf(")
                classNames.forEach { className ->
                    val instance = if (interfaceName == "IRouteRoot") "$className()" else className
                    appendLine("            $instance,")
                }
                appendLine("        )")
                appendLine("    }")
                appendLine("}")
            }
        outputFile.writeText(fileContent)
    }
}
