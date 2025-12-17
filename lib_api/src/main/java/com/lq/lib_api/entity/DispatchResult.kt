package com.lq.lib_api.entity

import com.lq.lib_api.interceptor.RouteRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class DispatchResult {

    data class Success( val request: RouteRequest): DispatchResult()

    data class Fail( val reason:String): DispatchResult()
}



