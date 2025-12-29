package com.lq.lib_api.degrade

import com.lq.lib_api.navigate.NavigateContext
import com.lq.lib_api.navigate.NavigateExecutor
import com.lq.lib_api.route.HRouterDelegate
import com.lq.lib_api.util.LogUtil

internal class FakeNavigateExecutor: NavigateExecutor {
    override fun execute(
        delegate: HRouterDelegate,
        navigateContext: NavigateContext
    ) {
        LogUtil.d("FakeNavigateExecutor : ${navigateContext.path}")
        DegradeManager.handleDegrade(
            navigateContext.path,
            Exception("fakeNavigateExecutor"),
            navigateContext.degradeContext
        )
    }
}