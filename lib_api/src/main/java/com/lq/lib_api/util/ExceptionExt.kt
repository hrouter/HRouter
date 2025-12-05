package com.lq.lib_api.util

import android.content.Context
import com.lq.lib_api.degrade.DegradeManager


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