package com.lq.lib_api.degrade

import com.lq.lib_api.entity.DegradeResult

interface IRouteDegrade {

    fun onLost(request: DegradeRequest): DegradeResult
}