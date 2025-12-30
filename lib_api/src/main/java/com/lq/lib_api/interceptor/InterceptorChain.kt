package com.lq.lib_api.interceptor

import com.lq.lib_api.entity.InterceptorResult


interface InterceptorChain {
    val routeContext: RouteContext

    suspend fun proceed(context: RouteContext = this.routeContext): InterceptorResult

    fun redirect(newPath: String): RouteContext{
        val context = routeContext.copy(
            request = routeContext.request.copy(path = newPath)
        )
        return context
    }


}


