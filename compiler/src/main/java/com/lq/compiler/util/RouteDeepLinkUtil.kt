package com.lq.compiler.util

import com.google.devtools.ksp.processing.KSPLogger

internal object RouteDeepLinkUtil {
    private val classMap = mutableMapOf<String, String>()

    private val linkMap = mutableMapOf<String, Array<String>>()

    fun classToPath(
        classInfo: String,
        path: String,
    ) {
        classMap[classInfo] = path
    }

    fun classToLinks(
        classInfo: String,
        links: Array<String>,
    ) {
        if (links.isNotEmpty()) {
            linkMap[classInfo] = links
        }
    }

    fun getLinkMapAndClassMap(logger: KSPLogger) {
        classMap.forEach {
            logger.warn("ClassMap  KEY : ${it.key} VALUE: ${it.value}")
        }
        linkMap.forEach {
            logger.warn("LinKMap  KEY : ${it.key} VALUE: ${it.value}")
        }
    }

    fun linkIsNotEmpty(): Boolean = linkMap.isNotEmpty()

    fun clear() {
        classMap.clear()
        linkMap.clear()
    }

    fun contractPathToLink(): Map<String, Array<String>> {
        val data = mutableMapOf<String, Array<String>>()
        linkMap.forEach {
            if (classMap.containsKey(it.key)) {
                val path = classMap[it.key]!!
                val links = it.value
                data[path] = links
            }
        }
        return data
    }

    /**
     * 笛卡尔积生成
     * 生成 schemes × hosts × paths 的所有组合
     */
    fun generateDeepLinkUrls(
        schemes: List<String>,
        hosts: List<String>,
        paths: List<String>,
    ): List<String> {
        // 处理默认值逻辑
        val effectiveSchemes = if (schemes.isEmpty()) listOf("") else schemes
        val effectiveHosts = if (hosts.isEmpty()) listOf("") else hosts
        val effectivePaths = if (paths.isEmpty()) listOf("") else paths

        return effectiveSchemes.flatMap { scheme ->
            effectiveHosts.flatMap { host ->
                effectivePaths.map { path ->
                    buildDeepLinkUrl(scheme, host, path)
                }
            }
        }
    }

    private fun buildDeepLinkUrl(
        scheme: String,
        host: String,
        path: String,
    ): String =
        when {
            // scheme://host/path 格式
            scheme.isNotEmpty() && host.isNotEmpty() && path.isNotEmpty() -> {
                "$scheme://$host${if (path.startsWith("/")) path else "/$path"}"
            }

            // scheme://host 格式（无path）
            scheme.isNotEmpty() && host.isNotEmpty() -> {
                "$scheme://$host"
            }

            // scheme:path 格式（如 myapp:home）
            scheme.isNotEmpty() && path.isNotEmpty() -> {
                "$scheme:${if (path.startsWith("/")) path.substring(1) else path}"
            }

            // 只有 path（作为相对路径）
            path.isNotEmpty() -> {
                path
            }

            // 只有 host
            host.isNotEmpty() -> {
                host
            }

            else -> {
                scheme
            }
        }
}
