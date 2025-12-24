package com.lq.gradletest

import android.content.Context
import com.lq.lib_annotation.RouterInterceptor
import com.lq.lib_api.entity.InterceptorResult
import com.lq.lib_api.interceptor.IRouteInterceptor
import com.lq.lib_api.interceptor.InterceptorChain

@RouterInterceptor(priority = 3,path = "/login/*")
class PermissionInterceptor: IRouteInterceptor {
    override fun init(context: Context) {

    }

    override suspend fun intercept(chain: InterceptorChain): InterceptorResult {
        var hasPermission = false
        if(!hasPermission) return InterceptorResult.Redirect(chain.context.copy(request = chain.context.request.copy(path = "/login/login")))
        return InterceptorResult.Continue
    }
}