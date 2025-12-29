package com.lq.lib_api.degrade

import com.lq.lib_api.entity.DegradeResult

class FakeDegrade: IRouteDegrade {

    override fun onLost(request: DegradeRequest): DegradeResult {
        return DegradeResult.Redirect(request.copy(newPath = "/degrade/degrade",reason = "degrade degrade"))
    }
}