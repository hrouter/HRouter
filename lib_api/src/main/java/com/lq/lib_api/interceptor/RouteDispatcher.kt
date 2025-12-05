package com.lq.lib_api.interceptor

object RouteDispatcher {

    suspend fun dispatch(request: RouteRequest): RouteResult {
        val chain = RealRouteChain(InterceptorManager.interceptors, 0, request)
        return chain.proceed(request)
    }
}