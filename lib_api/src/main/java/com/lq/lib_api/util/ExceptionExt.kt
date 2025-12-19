package com.lq.lib_api.util

import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.degrade.DegradeManager
import kotlinx.coroutines.CoroutineExceptionHandler




/*
* 路由降级协程处理器
* */
fun routeDegradeCoroutineHandler(context: DegradeContext, path: String) = CoroutineExceptionHandler { _, throwable ->
    if (throwable is Exception) {
        LogUtil.d("Route Degrade Coroutine Exception : $path")
        DegradeManager.handleDegrade(context,path,throwable)
    }
}


