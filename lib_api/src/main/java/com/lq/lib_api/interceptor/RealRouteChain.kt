package com.lq.lib_api.interceptor

import com.lq.lib_api.entity.RouteAction


class RealRouteChain(
    private val interceptors:List<IRouteInterceptor>,
    private val index: Int,
    override val request: RouteRequest
): InterceptorChain {

    override suspend fun proceed(request: RouteRequest): RouteAction {
        if (index >= interceptors.size) return RouteAction.Success
        val next = RealRouteChain( interceptors,index + 1, request)
        return when (val action = interceptors[index].intercept(next)) {
            RouteAction.Continue -> next.proceed(request)
            else -> action
        }
    }

}