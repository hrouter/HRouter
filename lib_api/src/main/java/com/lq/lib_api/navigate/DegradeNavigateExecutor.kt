package com.lq.lib_api.navigate

import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.interceptor.RouteDispatcher
import com.lq.lib_api.route.HRouterDelegate
import com.lq.lib_api.util.LogUtil

/*
* 降级执行器
* */
internal class DegradeNavigateExecutor : NavigateExecutor {
    override fun execute(
        delegate: HRouterDelegate,
        navigateContext: NavigateContext
    ) {
        val context = navigateContext.routeContext

        RouteDispatcher.dispatchAsync(
            context,
            onSuccess = { realPath ->
                val intentBuilder = delegate.buildIntent(realPath)
                LogUtil.d("navigateWithDegrade : $realPath")
                delegate.startActivity(intentBuilder)
            },
            onFail = { reason ->
                LogUtil.d("navigateWithDegrade reason : $reason")
                DegradeManager.handleDegrade(
                    navigateContext.path,
                    Exception(reason),
                    navigateContext.degradeContext
                )
            }, navigateContext.degradeContext
        )
    }
}