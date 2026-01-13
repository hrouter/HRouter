package com.lq.core.entity

import com.lq.core.degrade.DegradeContext
import com.lq.core.interceptor.RouteContext

sealed class DispatchResult {
    data class Success(
        val context: RouteContext,
    ) : DispatchResult()

    data class Fail(
        val reason: DispatchFailReason,
    ) : DispatchResult()
}

sealed class DispatchFailReason {
    object Intercepted : DispatchFailReason()

    data class LoopDetected(
        val path: String,
    ) : DispatchFailReason()

    data class Degrade(
        val degradeContext: DegradeContext,
        val reason: String? = "",
    ) : DispatchFailReason()

    data class Exception(
        val e: Throwable,
    ) : DispatchFailReason()
}
