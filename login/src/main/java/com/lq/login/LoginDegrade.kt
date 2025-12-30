package com.lq.login

import com.lq.lib_annotation.RouteDegrade
import com.lq.lib_api.degrade.IRouteDegrade
import com.lq.lib_api.degrade.DegradeRequest
import com.lq.lib_api.entity.DegradeResult
import com.lq.lib_api.util.LogUtil

/*
@RouteDegrade(priority = 3,path ="*" )
class LoginDegrade: IRouteDegrade {

    override fun onLost(request: DegradeRequest): DegradeResult {

        LogUtil.d("path:${request.newPath} reason:${request.reason}")

        return DegradeResult.Redirect(request.copy(newPath = "/degrade/degrade",reason = "degrade degrade"))
    }
}*/
