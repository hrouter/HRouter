package com.lq.core.interceptor

import android.content.Context
import com.lq.core.entity.InterceptorResult

class LogInterceptor : IRouteInterceptor {
    override fun init(context: Context) = Unit

    override suspend fun intercept(chain: InterceptorChain): InterceptorResult = InterceptorResult.Continue
}
