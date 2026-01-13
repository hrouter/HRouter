package com.lq.core.interceptor

import com.lq.core.entity.InterceptorResult

class RealRouteChain(
    private val interceptors: List<IRouteInterceptor>,
    private val index: Int,
    override val routeContext: RouteContext,
) : InterceptorChain {
    override suspend fun proceed(context: RouteContext): InterceptorResult {
        if (index >= interceptors.size) return InterceptorResult.Continue

        val next = RealRouteChain(interceptors, index + 1, context.copy(attempts = context.attempts + 1))

        return when (val interceptorResult = interceptors[index].intercept(next)) {
            InterceptorResult.Continue -> next.proceed(context)
            else -> interceptorResult
        }
    }
}
