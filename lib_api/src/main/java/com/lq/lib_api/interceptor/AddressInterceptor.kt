package com.lq.lib_api.interceptor

import android.content.Context
import com.lq.lib_annotation.RouterInterceptor
import com.lq.lib_api.entity.RouteAction

@RouterInterceptor(priority = Int.MIN_VALUE)
class AddressInterceptor : IRouteInterceptor{

    private val visitedPaths = mutableSetOf<String>()

    override fun init(context: Context) {

    }

    override suspend fun intercept(chain: InterceptorChain): RouteAction {
        val req = chain.request

        if (req.path in visitedPaths) {
            return RouteAction.Continue
        }
        visitedPaths.add(req.path)

        if (!req.path.startsWith("app://")) return RouteAction.Fail("非法地址")

        return chain.proceed(req)
    }
}