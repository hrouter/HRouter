package com.lq.lib_api.degrade

import android.content.Context
import com.lq.lib_annotation.degrade.IRouteDegrade

internal object DegradeFactory {

    private val interceptorCache = mutableMapOf<String, IRouteDegrade>()

    fun create(context: Context,className: String): IRouteDegrade {
        return interceptorCache.getOrPut(className) {
            val clazz = Class.forName(className)
            val instance = clazz.getDeclaredConstructor().newInstance() as IRouteDegrade
            instance
        }
    }
}