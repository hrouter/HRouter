package com.lq.core.interceptor

data class RouteContext(
    val request: RouteRequest,
    val attempts: Int,
) {
    fun redirect(newPath: String): RouteRequest = request.copy(path = newPath)
}
