package com.lq.lib_api.util

import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.navigate.NavigateContext
import kotlinx.coroutines.CoroutineExceptionHandler




/*
* 路由降级协程处理器
* */
internal fun routeDegradeCoroutineHandler(navigateContext: NavigateContext) = CoroutineExceptionHandler { _, throwable ->
    if (throwable is Exception) {
        val context = navigateContext.routeContext
        val degradeContext = navigateContext.degradeContext
        LogUtil.d("Route Degrade Coroutine Exception : ${context.request.path} ${throwable.message}")
        DegradeManager.handleDegrade(context.request.path,throwable,degradeContext)
    }
}


