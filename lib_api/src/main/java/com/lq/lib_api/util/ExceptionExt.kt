package com.lq.lib_api.util

import android.content.Context
import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.entity.DispatchResult
import com.lq.lib_api.interceptor.RouteDispatcher
import com.lq.lib_api.interceptor.RouteRequest
import kotlinx.coroutines.CoroutineExceptionHandler


/**
 * 执行可能失败的行为，自动进行降级处理
 * @param R 返回值类型
 * @param context 上下文
 * @param path 请求路径（用于降级标识）
 * @return 正常结果或降级值
 */
internal fun <R> (() -> R).withDegrade(
    context: Context,
    path: String,
): R? {
    return try {
        this()
    } catch (e: Exception) {
//        DegradeManager.handleDegrade(path, e.message ?: e.javaClass.simpleName, context)
        null
    }
}

// 自定义扩展函数
suspend inline fun <T> suspendRunCatching(crossinline block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

/*
* 路由降级协程处理器
* */
fun routeDegradeCoroutineHandler(context: DegradeContext, path: String) = CoroutineExceptionHandler { _, throwable ->
    if (throwable is Exception) {
        LogUtil.d("Route Degrade Coroutine Exception : $path")
        DegradeManager.handleDegrade(context,path,throwable)
    }
}

/*
* 安全跳转
* */
suspend fun safeNavigateAsync(
    request: RouteRequest
): Result<DispatchResult.Success> {
    return when (val result = RouteDispatcher.dispatch(request)) {
        is DispatchResult.Success -> Result.success(result)
        is DispatchResult.Fail -> Result.failure(Exception(result.reason))
    }
}
