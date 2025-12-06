package com.lq.lib_api.interceptor

object RouteDispatcher {

    suspend fun dispatch(request: RouteRequest): RouteResult {
        val chain = RealRouteChain(InterceptorManager.interceptors, 0, request)
        val path = request.path
        if(path in request.visitedPaths){
            return RouteResult(allow = false,reason = "path loop")
        }
        request.visitedPaths += path
        return chain.proceed(request)
    }
}