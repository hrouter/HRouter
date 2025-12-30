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
        target.plugins.withId("com.android.base") {
            val android = target.extensions.getByName("android") as BaseExtension

            android.sourceSets.getByName("main").java.srcDirs(
                target.layout.buildDirectory.dir("generated/route"),
                target.layout.buildDirectory.dir("generated/interceptor"),
                target.layout.buildDirectory.dir("generated/degrade"),
                target.layout.buildDirectory.dir("generated/deeplink")
            )
        }


        target.plugins.withId("com.android.application") {
            target.afterEvaluate {
                // 应用 KSP 插件
                target.plugins.apply("com.google.devtools.ksp")

                // 注册构建路由索引任务
                val buildRoute : TaskProvider<ContractTask> = target.tasks.register("buildRoute", ContractTask::class.java) {
                    group = "HRouter"
                    description = "构建路由/拦截器/降级/DeepLink 索引"
                }


                // 找到所有 KSP Kotlin 任务（兼容 debug/release）
                tasks.matching { it.name.startsWith("ksp") && it.name.endsWith("Kotlin") }
                    .configureEach {
                        finalizedBy(buildRoute.get())
                    }

            }
        }
    }
}