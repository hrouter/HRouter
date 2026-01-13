package com.lq.core.degrade

import com.lq.core.navigate.NavigateContext
import com.lq.core.navigate.NavigateExecutor
import com.lq.core.route.HRouterDelegate
import com.lq.core.util.LogUtil

internal class FakeNavigateExecutor : NavigateExecutor {
    override fun execute(
        delegate: HRouterDelegate,
        navigateContext: NavigateContext,
    ) {
        LogUtil.d("FakeNavigateExecutor : ${navigateContext.path}")
        DegradeManager.handleDegrade(
            navigateContext.path,
            Exception("fakeNavigateExecutor"),
            navigateContext.degradeContext,
        )
    }
}
