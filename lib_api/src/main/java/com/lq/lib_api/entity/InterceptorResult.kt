package com.lq.lib_api.entity

import com.lq.lib_api.interceptor.RouteContext


sealed class InterceptorResult{

    object Continue: InterceptorResult()

    data class Redirect(val newContext: RouteContext): InterceptorResult()

    data class Fail(val reason: String): InterceptorResult()


}