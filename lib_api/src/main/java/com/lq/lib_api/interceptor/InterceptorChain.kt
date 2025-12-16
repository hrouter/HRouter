package com.lq.lib_api.interceptor

import com.lq.lib_api.entity.RouteAction


interface InterceptorChain {
    val request: RouteRequest

    suspend fun proceed(request: RouteRequest = this.request): RouteAction
}


