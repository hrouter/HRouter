package com.lq.core.interceptor

import com.lq.core.degrade.DegradeContext
import com.lq.core.entity.DispatchFailReason
import com.lq.core.entity.DispatchResult
import com.lq.core.entity.InterceptorResult
import com.lq.core.navigate.NavigateContext
import com.lq.core.util.LogUtil
import com.lq.core.util.routeDegradeCoroutineHandler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal object RouteDispatcher {
    private const val MAX_REDIRECT = 3

    var ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    /**
     * 异步调度路由请求
     * @param context 路由请求上下文
     * @param onSuccess 成功回调，返回最终路径
     * @param onFail 失败回调，返回失败原因
     */
    @Suppress("TooGenericExceptionCaught")
    fun dispatchAsync(
        context: NavigateContext,
        onSuccess: (realPath: String) -> Unit,
        onFail: (reason: DispatchFailReason) -> Unit,
    ) {
        val degradeContext = context.degradeContext // 有降级上下文，代表此次跳转为降级跳转，降级中失败不继续降级
        // 使用协程在 IO 线程调度
        CoroutineScope(ioDispatcher + SupervisorJob() + routeDegradeCoroutineHandler(context))
            .launch {
                try {
                    when (val result = dispatch(context.routeContext)) {
                        is DispatchResult.Success -> {
                            val realPath = result.context.request.path
                            withContext(mainDispatcher) {
                                onSuccess(realPath)
                            }
                        }

                        is DispatchResult.Fail -> {
                            withContext(mainDispatcher) {
                                if (degradeContext != null) {
                                    onFail(DispatchFailReason.Degrade(degradeContext))
                                } else {
                                    onFail(result.reason)
                                }
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    e.printStackTrace()
                    handleFail(degradeContext, e, onFail)
                }
            }
    }

    suspend fun dispatch(context: RouteContext): DispatchResult {
        var currentContext = context

        while (true) {
            val interceptors =
                InterceptorManager.getInterceptorsForRequest(currentContext.request.path)

            if (interceptors.isEmpty()) return DispatchResult.Success(currentContext)

            val chain = RealRouteChain(interceptors, 0, currentContext)

            LogUtil.d("Interceptor Result : ${interceptors.first()} ${currentContext.request.path} ${currentContext.attempts} ")

            when (val action = chain.proceed(currentContext)) {
                is InterceptorResult.Continue -> {
                    return DispatchResult.Success(currentContext)
                }

                is InterceptorResult.Fail -> {
                    return DispatchResult.Fail(DispatchFailReason.Intercepted)
                }

                // 被拦截器拦截

                is InterceptorResult.Redirect -> {
                    if (action.newContext.attempts >= MAX_REDIRECT) {
                        return DispatchResult.Fail(DispatchFailReason.LoopDetected(currentContext.request.path))
                    }
                    currentContext = action.newContext
                    LogUtil.d("Redirect Context :${currentContext.request.path} ${currentContext.attempts}")
                    continue
                }
            }
        }
    }

    private suspend fun handleFail(
        degradeContext: DegradeContext?,
        e: Exception,
        onFail: (reason: DispatchFailReason) -> Unit,
    ) {
        withContext(mainDispatcher) {
            if (degradeContext != null) {
                onFail(DispatchFailReason.Degrade(degradeContext, e.message ?: "Unknow Error"))
            } else {
                onFail(DispatchFailReason.Exception(e))
            }
        }
    }
}
