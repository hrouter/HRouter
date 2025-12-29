package com.lq.lib_api.navigate

import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.interceptor.RouteRequest
import com.lq.lib_api.route.HRouterDelegate

internal interface NavigateExecutor {

    fun execute(delegate: HRouterDelegate, navigateContext: NavigateContext)
}