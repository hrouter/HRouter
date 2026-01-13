package com.lq.core.interceptor

import android.content.Context
import com.lq.core.entity.InterceptorResult

class TestInterceptor(
    val name: String,
) : IRouteInterceptor {
    override fun init(context: Context) = Unit

    override suspend fun intercept(chain: InterceptorChain): InterceptorResult = InterceptorResult.Continue
}
