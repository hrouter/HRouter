package com.lq.lib_api.interceptor

import com.lq.lib_api.entity.InterceptorResult
import com.lq.lib_api.util.LogUtil


class RealRouteChain(
    private val interceptors:List<IRouteInterceptor>,
    private val index: Int,
    override val context: RouteContext
): InterceptorChain {

    override suspend fun proceed(context: RouteContext): InterceptorResult {
        if (index >= interceptors.size) return InterceptorResult.Continue

        val next = RealRouteChain( interceptors,index + 1, context.copy(attempts = context.attempts + 1))
        LogUtil.d("Interceptor Result : ${interceptors[index]} ${context.request.path} ${context.attempts} ")

        return when (val interceptorResult = interceptors[index].intercept(next)) {
            InterceptorResult.Continue -> next.proceed(context)
            else -> interceptorResult
        }
    }

}