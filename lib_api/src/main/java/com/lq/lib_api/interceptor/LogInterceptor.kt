package com.lq.lib_api.interceptor

import android.content.Context
import com.lq.lib_api.entity.InterceptorResult

class LogInterceptor: IRouteInterceptor {

    override fun init(context: Context) {

    }

    override suspend fun intercept(chain: InterceptorChain): InterceptorResult {
        return InterceptorResult.Continue
    }
}