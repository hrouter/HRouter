package com.lq.core.degrade

import com.lq.annotation.RouteDegrade
import com.lq.core.entity.DegradeResult

@RouteDegrade(priority = Int.MAX_VALUE, path = "*")
class DefaultDegrade : IRouteDegrade {
    override fun onLost(request: DegradeRequest): DegradeResult = DegradeResult.Ignore
}
