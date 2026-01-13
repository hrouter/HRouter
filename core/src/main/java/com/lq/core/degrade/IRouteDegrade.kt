package com.lq.core.degrade

import com.lq.core.entity.DegradeResult

interface IRouteDegrade {
    fun onLost(request: DegradeRequest): DegradeResult
}
