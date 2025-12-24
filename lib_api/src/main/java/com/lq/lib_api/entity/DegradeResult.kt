package com.lq.lib_api.entity

import com.lq.lib_api.degrade.DegradeRequest

sealed class DegradeResult {

    data class Redirect(val newRequest: DegradeRequest): DegradeResult()

    object Ignore: DegradeResult()
}