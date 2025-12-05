package com.lq.login

import android.content.Context
import com.lq.lib_annotation.RouterInterceptor
import com.lq.lib_api.interceptor.IRouteInterceptor
import com.lq.lib_api.interceptor.InterceptorChain
import com.lq.lib_api.interceptor.RouteResult
import com.lq.lib_api.util.LogUtil

@RouterInterceptor(priority = 1, path = "/login")
class LoginInterceptor: IRouteInterceptor {

    override val whiteList: Set<String>
        get() = setOf("/login/login")

    override fun init(context: Context) {

    }

    override suspend fun intercept(chain: InterceptorChain): RouteResult {
        if(chain.request.path == "/login/login"){
            LogUtil.d("登录拦截器")
            return RouteResult(allow = false, reason = "请先登录", redirect = "/login/test")
        }
        return chain.proceed(chain.request)
    }


}