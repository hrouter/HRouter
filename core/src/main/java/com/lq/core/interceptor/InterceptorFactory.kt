package com.lq.core.interceptor

import android.content.Context

internal object InterceptorFactory {
    private val interceptorCache = mutableMapOf<String, IRouteInterceptor>()

    /*
     * 反射去获取
     * */
    fun create(
        context: Context,
        className: String,
    ): IRouteInterceptor =
        interceptorCache.getOrPut(className) {
            val clazz = Class.forName(className)
            val instance = clazz.getDeclaredConstructor().newInstance() as IRouteInterceptor
            instance.init(context)
            instance
        }
}
