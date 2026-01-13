package com.lq.core.entity

import com.lq.core.interceptor.RouteContext

sealed class InterceptorResult {
    object Continue : InterceptorResult()

    data class Redirect(
        val newContext: RouteContext,
    ) : InterceptorResult()

    data class Fail(
        val reason: String,
    ) : InterceptorResult()
}
