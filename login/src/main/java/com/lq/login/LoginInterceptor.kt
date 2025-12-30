package com.lq.login

import android.content.Context
import com.lq.lib_annotation.RouterInterceptor
import com.lq.lib_api.entity.InterceptorResult
import com.lq.lib_api.interceptor.IRouteInterceptor
import com.lq.lib_api.interceptor.InterceptorChain

@RouterInterceptor(priority = 1, path = "/login/test")
class LoginInterceptor : IRouteInterceptor {

    override fun init(context: Context) {

    }

    override suspend fun intercept(chain: InterceptorChain): InterceptorResult {
        val isLogin = false
        if (!isLogin) return InterceptorResult.Redirect(chain.redirect("/login/login"))
        return InterceptorResult.Continue
    }

}