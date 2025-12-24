package com.lq.lib_api.entity

import com.lq.lib_api.interceptor.RouteContext

sealed class DispatchResult {

    data class Success( val context: RouteContext): DispatchResult()

    data class Fail( val reason:String): DispatchResult()
}



