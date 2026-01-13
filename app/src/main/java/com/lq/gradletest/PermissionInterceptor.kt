package com.lq.gradletest

import android.content.Context
import com.lq.annotation.RouterInterceptor
import com.lq.core.entity.InterceptorResult
import com.lq.core.interceptor.IRouteInterceptor
import com.lq.core.interceptor.InterceptorChain

@RouterInterceptor(priority = 3, path = "/login/*")
class PermissionInterceptor : IRouteInterceptor {
    override fun init(context: Context) {
    }

    override suspend fun intercept(chain: InterceptorChain): InterceptorResult {
        var hasPermission = true
        if (!hasPermission) return InterceptorResult.Redirect(chain.redirect("/login/login"))
        return InterceptorResult.Continue
    }
}
