package com.lq.compiler.util

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

internal object MetaInfUtil {
    fun writeMetaInf(
        codeGenerator: CodeGenerator,
        content: String,
        moduleName: String = "default",
        packageName: String = "META-INF/",
    ) {
        val file =
            codeGenerator.createNewFile(
                Dependencies(aggregating = true),
                packageName = packageName,
                fileName = moduleName,
                extensionName = "",
            )
        file.bufferedWriter().use { writer ->
            writer.write(content)
            writer.newLine()
        }
    }
}
