package com.lq.core.degrade

import com.lq.core.entity.DegradeResult

class FakeDegrade : IRouteDegrade {
    override fun onLost(request: DegradeRequest): DegradeResult =
        DegradeResult.Redirect(request.copy(newPath = "/degrade/degrade", reason = "degrade degrade"))
}
