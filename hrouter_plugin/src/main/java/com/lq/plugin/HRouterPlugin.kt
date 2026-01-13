package com.lq.plugin

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import kotlin.jvm.java

/**
 * Author: Lq
 * Date: 2025/4/29
 * Description:  MyPlugin
 */

class HRouterPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withId("com.google.devtools.ksp") {
            target.extensions.configure(com.google.devtools.ksp.gradle.KspExtension::class.java) {
                arg("HROUTER_MODULE_NAME", target.name)
            }
        }

        target.plugins.withId("com.android.application") {
            val androidComponents = target.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

            androidComponents.onVariants { variant ->
                val vName = variant.name
                val capitalizedName = vName.replaceFirstChar { it.uppercase() }
                val taskName = "build${capitalizedName}Route"

                val taskProvider =
                    target.tasks.register(taskName, ContractTask::class.java) {
                        this.vName = vName
                        this.customOutputDir.set(target.layout.buildDirectory.dir("generated/hrouter/$vName"))

                        dependsOn("ksp${capitalizedName}Kotlin")
                    }

                target.tasks.matching { it.name == "compile${capitalizedName}Kotlin" }.configureEach {
                    dependsOn(taskProvider)
                }

                val outputDirFile =
                    File(
                        target.layout.buildDirectory.asFile
                            .get(),
                        "generated/hrouter/$vName",
                    )
                val appExtension = target.extensions.findByType(com.android.build.gradle.AppExtension::class.java)
                appExtension
                    ?.sourceSets
                    ?.getByName(vName)
                    ?.java
                    ?.srcDir(outputDirFile)
//                variant.sources.java?.addGeneratedSourceDirectory(taskProvider) { it.customOutputDir }
            }
        }
    }
}
