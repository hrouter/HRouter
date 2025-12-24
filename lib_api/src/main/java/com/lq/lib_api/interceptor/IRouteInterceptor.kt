package com.lq.lib_api.interceptor

import android.content.Context
import com.lq.lib_api.entity.InterceptorResult


interface IRouteInterceptor {

    val whiteList :Set<String> get() = emptySet()

    fun init(context: Context)

    suspend fun intercept(chain: InterceptorChain): InterceptorResult
}

