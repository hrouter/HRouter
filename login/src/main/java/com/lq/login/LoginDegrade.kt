package com.lq.login

import com.lq.lib_annotation.RouteDegrade
import com.lq.lib_annotation.degrade.IRouteDegrade
import com.lq.lib_api.util.LogUtil

@RouteDegrade(priority = 3)
class LoginDegrade: IRouteDegrade {
    override fun onLost(
        path: String,
        reason: String,
    ): Boolean {
        LogUtil.d("path:$path reason:$reason")
        //重定向
        return true
    }
}