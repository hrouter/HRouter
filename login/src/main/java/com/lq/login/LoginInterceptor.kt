package com.lq.login

import android.content.Context
import com.lq.lib_annotation.RouterInterceptor
import com.lq.lib_api.interceptor.IRouteInterceptor
import com.lq.lib_api.interceptor.InterceptorChain
import com.lq.lib_api.entity.RouteAction

@RouterInterceptor(priority = 1, path = "/login/login")
class LoginInterceptor : IRouteInterceptor {

    override val whiteList: Set<String>
        get() = setOf("/login/login")

    override fun init(context: Context) {

    }

    override suspend fun intercept(chain: InterceptorChain): RouteAction {
        val isLogin = false
        if (!isLogin) return RouteAction.Redirect(chain.request.copy(path = "/login/test"))
        return chain.proceed(chain.request)
    }

}