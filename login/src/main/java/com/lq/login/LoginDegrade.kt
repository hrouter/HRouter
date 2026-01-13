package com.lq.login

/*
@RouteDegrade(priority = 3,path ="*" )
class LoginDegrade: IRouteDegrade {

    override fun onLost(request: DegradeRequest): DegradeResult {

        LogUtil.d("path:${request.newPath} reason:${request.reason}")

        return DegradeResult.Redirect(request.copy(newPath = "/degrade/degrade",reason = "degrade degrade"))
    }
}*/
