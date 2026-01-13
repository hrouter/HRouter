package com.lq.core.interceptor

import android.content.Context
import com.lq.core.entity.InterceptorResult

interface IRouteInterceptor {
    val whiteList: Set<String> get() = emptySet()

    fun init(context: Context)

    suspend fun intercept(chain: InterceptorChain): InterceptorResult
}
