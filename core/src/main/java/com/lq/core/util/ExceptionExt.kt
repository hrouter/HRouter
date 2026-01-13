package com.lq.core.util

import com.lq.core.degrade.DegradeManager
import com.lq.core.navigate.NavigateContext
import kotlinx.coroutines.CoroutineExceptionHandler

/*
* 路由降级协程处理器
* */
internal fun routeDegradeCoroutineHandler(navigateContext: NavigateContext) =
    CoroutineExceptionHandler { _, throwable ->
        if (throwable is Exception) {
            val context = navigateContext.routeContext
            val degradeContext = navigateContext.degradeContext
            LogUtil.d("Route Degrade Coroutine Exception : ${context.request.path} ${throwable.message}")
            DegradeManager.handleDegrade(context.request.path, throwable, degradeContext)
        }
    }
