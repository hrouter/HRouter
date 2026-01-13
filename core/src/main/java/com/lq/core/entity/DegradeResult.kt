package com.lq.core.entity

import com.lq.core.degrade.DegradeRequest

sealed class DegradeResult {
    data class Redirect(
        val newRequest: DegradeRequest,
    ) : DegradeResult()

    object Ignore : DegradeResult()
}
