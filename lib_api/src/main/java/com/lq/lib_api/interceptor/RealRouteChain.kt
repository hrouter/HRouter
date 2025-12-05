package com.lq.lib_api.interceptor


class RealRouteChain(
    private val interceptors: List<IRouteInterceptor>,
    private val index: Int,
    override val request: RouteRequest
): InterceptorChain {

    override suspend fun proceed(request: RouteRequest): RouteResult {
        if (index >= interceptors.size) {
            return RouteResult(allow = true)
        }
        val next = RealRouteChain(interceptors, index + 1, request)
        return interceptors[index].intercept(next)
    }



}