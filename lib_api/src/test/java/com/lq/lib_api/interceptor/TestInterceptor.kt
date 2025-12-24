package com.lq.lib_api.interceptor

import android.content.Context
import com.lq.lib_api.entity.InterceptorResult

class TestInterceptor(val name: String) : IRouteInterceptor{
    override fun init(context: Context) {

    }

    override suspend fun intercept(chain: InterceptorChain): InterceptorResult {
        return InterceptorResult.Continue
    }

}