package com.lq.lib_api.interceptor

import android.content.Context

internal object InterceptorFactory {
    private val interceptorCache = mutableMapOf<String, IRouteInterceptor>()

    fun create(context: Context,className: String): IRouteInterceptor{
        return interceptorCache.getOrPut(className) {
            val clazz = Class.forName(className)
            val instance = clazz.getDeclaredConstructor().newInstance() as IRouteInterceptor
            instance.init(context)
            instance
        }
    }
}