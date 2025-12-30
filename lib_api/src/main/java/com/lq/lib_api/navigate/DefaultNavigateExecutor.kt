package com.lq.lib_api.navigate

import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.entity.DispatchFailReason
import com.lq.lib_api.exception.DegradeFailException
import com.lq.lib_api.interceptor.RouteDispatcher
import com.lq.lib_api.route.HRouterDelegate
import com.lq.lib_api.util.LogUtil

internal class DefaultNavigateExecutor : NavigateExecutor {
    override fun execute(
        delegate: HRouterDelegate,
        navigateContext: NavigateContext
    ) {
        RouteDispatcher.dispatchAsync(
            navigateContext,
            onSuccess = { realPath ->
                val intentBuilder = delegate.buildIntent(realPath)
                LogUtil.d("Navigate Success : $realPath")
                delegate.startActivity(intentBuilder)
            },
            onFail = { reason ->
                when (reason) {
                    is DispatchFailReason.Intercepted,
                    is DispatchFailReason.LoopDetected -> {
                        LogUtil.i("Navigate Blocked :$reason")
                    }
                    is DispatchFailReason.Degrade -> { //降级失败
                        DegradeManager.handleDegrade(
                            navigateContext.path,
                            Exception(DegradeFailException(navigateContext.path)),
                            navigateContext.degradeContext
                        )
                    }
                    is DispatchFailReason.Exception -> {
                        DegradeManager.handleDegrade(
                            navigateContext.path,
                            Exception(reason.e)
                        )
                    }
                }
            })
    }
}