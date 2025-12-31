package com.lq.hrouter_plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import kotlin.jvm.java
import org.gradle.api.tasks.TaskProvider

/**
 * Author: Lq
 * Date: 2025/4/29
 * Description:  MyPlugin
 */
class HRouterPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withId("com.android.library") {
            val android = target.extensions.getByName("android") as com.android.build.gradle.BaseExtension

            // 使用 AGP 新版 Variant API
            target.extensions.findByType(
                com.android.build.api.variant.LibraryAndroidComponentsExtension::class.java
            )?.onVariants { variant ->
                // 输出目录
                val generatedDirs = listOf("route", "interceptor", "degrade", "deeplink").map { folder ->
                    target.layout.buildDirectory.dir("generated/$folder").get().asFile
                }

                // 挂载到当前 variant
                android.sourceSets.getByName("main").java.srcDirs(generatedDirs)
            }
        }

        target.plugins.withId("com.android.application") {

            target.afterEvaluate {
                // 应用 KSP 插件

                // 注册生成路由索引的任务
                val buildRouteTask = target.tasks.register("buildRoute", ContractTask::class.java) {
                    group = "HRouter"
                    description = "生成路由/拦截器/降级/DeepLink索引"
                }

                // 找到所有 KSP Kotlin 任务
                target.tasks.matching { it.name.startsWith("ksp") && it.name.endsWith("Kotlin") }
                    .configureEach {
                        // 保证 KSP 编译后执行 buildRoute
                        finalizedBy(buildRouteTask.get())
                    }

                // 将生成目录挂载到 sourceSet
                val android = target.extensions.getByName("android") as BaseExtension
                val generatedDirs = listOf("route", "interceptor", "degrade", "deeplink").map { folder ->
                    target.layout.buildDirectory.dir("generated/$folder").get().asFile
                }
                android.sourceSets.getByName("main").java.srcDirs(generatedDirs)
            }
        }
    }
}