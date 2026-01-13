package com.lq.core.navigate

import com.lq.core.route.HRouterDelegate

internal interface NavigateExecutor {
    fun execute(
        delegate: HRouterDelegate,
        navigateContext: NavigateContext,
    )
}
