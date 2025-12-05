package com.lq.lib_api.interceptor


interface InterceptorChain {
    val request: RouteRequest

    suspend fun proceed(request: RouteRequest = this.request): RouteResult
}


