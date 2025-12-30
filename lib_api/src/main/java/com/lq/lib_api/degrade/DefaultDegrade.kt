package com.lq.lib_api.degrade

import com.lq.lib_annotation.RouteDegrade
import com.lq.lib_api.entity.DegradeResult

@RouteDegrade(priority = Int.MAX_VALUE,path ="*" )
class DefaultDegrade: IRouteDegrade {

    override fun onLost(request: DegradeRequest): DegradeResult {
        return DegradeResult.Ignore
    }
}
