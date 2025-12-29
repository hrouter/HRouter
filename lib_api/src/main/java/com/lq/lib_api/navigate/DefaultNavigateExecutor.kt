package com.lq.lib_api.navigate

import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.interceptor.RouteDispatcher
import com.lq.lib_api.route.HRouterDelegate
import com.lq.lib_api.util.LogUtil

internal class DefaultNavigateExecutor : NavigateExecutor {
    override fun execute(
        delegate: HRouterDelegate,
        navigateContext: NavigateContext
    ) {
        val routeContext = navigateContext.routeContext
        RouteDispatcher.dispatchAsync(
            routeContext,
            onSuccess = { realPath ->
                val intentBuilder = delegate.buildIntent(realPath)
                LogUtil.d("navigateWithDegrade : $realPath")
                delegate.startActivity(intentBuilder)
            },
            onFail = { reason ->
                DegradeManager.handleDegrade(
                    navigateContext.path,
                    Exception(reason)
                )
            })
    }
}