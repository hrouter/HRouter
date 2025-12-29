package com.lq.lib_api.util

import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.interceptor.RouteContext
import kotlinx.coroutines.CoroutineExceptionHandler




/*
* 路由降级协程处理器
* */
fun routeDegradeCoroutineHandler( context: RouteContext,degradeContext: DegradeContext?=null) = CoroutineExceptionHandler { _, throwable ->
    if (throwable is Exception) {
        LogUtil.d("Route Degrade Coroutine Exception : ${context.request.path} ${throwable.message}")
        DegradeManager.handleDegrade(context.request.path,throwable,degradeContext)
    }
}


