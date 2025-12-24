package com.lq.lib_api.interceptor

import com.lq.lib_api.entity.InterceptorResult


interface InterceptorChain {
    val context: RouteContext

    suspend fun proceed(context: RouteContext = this.context): InterceptorResult

    fun redirect(newPath: String): RouteContext{
        context.copy(
            request = context.request.copy(path = newPath)
        )
//        this.context = context
        return context
    }


}


