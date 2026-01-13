package com.lq.login

import android.content.Context
import com.lq.annotation.RouterInterceptor
import com.lq.core.entity.InterceptorResult
import com.lq.core.interceptor.IRouteInterceptor
import com.lq.core.interceptor.InterceptorChain

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
