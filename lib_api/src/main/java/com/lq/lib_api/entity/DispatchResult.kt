package com.lq.lib_api.entity

import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.interceptor.RouteContext

sealed class DispatchResult {

    data class Success( val context: RouteContext): DispatchResult()

    data class Fail( val reason: DispatchFailReason): DispatchResult()
}



sealed class DispatchFailReason{

    object Intercepted : DispatchFailReason()

    data class LoopDetected(val path :String): DispatchFailReason()

    data class Degrade(val degradeContext: DegradeContext,val reason:String?="") : DispatchFailReason()

    data class Exception(val e: Throwable): DispatchFailReason()

}