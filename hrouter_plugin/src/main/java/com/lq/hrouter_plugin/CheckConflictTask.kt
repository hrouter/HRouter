package com.lq.hrouter_plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URLClassLoader
import kotlin.jvm.java

abstract class CheckConflictTask: DefaultTask() {

    init {
        group = "RouteGradle"
        description = "Check Route Conflict"
        dependsOn("kspDebugKotlin")
    }


    @TaskAction
    fun runAction(){
        checkRouteConflict()
    }

    private fun checkRouteConflict(){
        val classesDir = File(project.buildDir, "tmp/kotlin-classes/debug")

        if (!classesDir.exists()) {
            println("HRouterContractTask: no classes dir, skip")
            return
        }

        runCheck(classesDir)
    }

    private fun runCheck(classesDir: File) {
        val classLoader = URLClassLoader(
            arrayOf(classesDir.toURI().toURL()),
            javaClass.classLoader
        )

        val allPaths = mutableMapOf<String, MutableList<String>>()

        classesDir.walkTopDown()
            .filter { it.isFile && it.name.endsWith(".class") }
            .forEach { classFile ->
                val className = classFile
                    .relativeTo(classesDir)
                    .path
                    .removeSuffix(".class")
                    .replace(File.separatorChar, '.')

                tryLoadGroup(classLoader, className, allPaths)
            }

        reportConflict(allPaths)
    }

    private fun tryLoadGroup(
        classLoader: ClassLoader,
        className: String,
        allPaths: MutableMap<String, MutableList<String>>
    ) {
        val clazz = try {
            classLoader.loadClass(className)
        } catch (e: Throwable) {
            return
        }

      /*  if (!IRouteGroup::class.java.isAssignableFrom(clazz)) return
        if (clazz.isInterface || clazz.isEnum) return

        val instance = try {
            clazz.getDeclaredConstructor().newInstance() as IRouteGroup
        } catch (e: Throwable) {
            return
        }

        val groupMap = mutableMapOf<String, RouteMeta>()
        instance.loadInto(groupMap)

        groupMap.keys.forEach { path ->
            allPaths
                .getOrPut(path) { mutableListOf() }
                .add(clazz.name)
        }*/
    }

    private fun reportConflict(allPaths: Map<String, List<String>>) {
        val conflicts = allPaths.filter { it.value.size > 1 }

        if (conflicts.isEmpty()) {
            println("HRouterContractTask: no route conflict")
            return
        }

        val msg = buildString {
            appendLine("🚨 HRouter 路由冲突检测失败")
            conflicts.forEach { (path, owners) ->
                appendLine()
                appendLine("Path: $path")
                owners.forEach {
                    appendLine("  -> $it")
                }
            }
        }

        throw RuntimeException(msg)
    }


}