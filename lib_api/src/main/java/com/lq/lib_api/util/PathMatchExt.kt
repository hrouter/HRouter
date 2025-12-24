package com.lq.lib_api.util


/**
 * 异步调度路由请求
 * @param path 请求对比的地址
 * @sample /login/login.pathMatches(/login/master)
 * 前者为页面地址，后者为拦截器 降级处理器的过滤地址
 */
fun String.pathMatches(pattern: String): Boolean {
    if (pattern.isEmpty() || pattern == "*") return true
    if (this == pattern) return true
    val requestParts = this.trim('/').split("/")
    val patternParts = pattern.trim('/').split("/")
    if (requestParts.size != patternParts.size) return false
    for (i in requestParts.indices) {
        val req = requestParts[i]
        val pat = patternParts[i]
        if (pat != "*" && pat != req) return false
    }
    return true
}

private fun String.matchZRegex() = Regex("^" + replace(".", "\\.").replace("*", ".*") + "$")

