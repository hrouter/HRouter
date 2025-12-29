package com.lq.lib_api.interceptor

import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.entity.DispatchResult
import com.lq.lib_api.entity.InterceptorResult
import com.lq.lib_api.util.LogUtil
import com.lq.lib_api.util.routeDegradeCoroutineHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object RouteDispatcher {
    private const val MAX_REDIRECT = 3

    var ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    var mainDispatcher: CoroutineDispatcher = Dispatchers.Main


    /**
     * 异步调度路由请求
     * @param context 路由请求上下文
     * @param onSuccess 成功回调，返回最终路径
     * @param onFail 失败回调，返回失败原因
     * @param degradeContext 降级上下文，避免循环降级
     */
    fun dispatchAsync(
        context: RouteContext,
        onSuccess: (realPath: String) -> Unit,
        onFail: (reason: String) -> Unit,
        degradeContext: DegradeContext?=null
    ) {
        // 使用协程在 IO 线程调度
        CoroutineScope(ioDispatcher+ SupervisorJob() + routeDegradeCoroutineHandler(context,degradeContext))
            .launch {
                try {
                    when (val result = dispatch(context)) {
                        is DispatchResult.Success -> {
                            val realPath = result.context.request.path
                            withContext(mainDispatcher) {
                                onSuccess(realPath)
                            }
                        }

                        is DispatchResult.Fail -> {
                            withContext(mainDispatcher) {
                                onFail(result.reason)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(mainDispatcher) {
                        onFail(e.message ?: "Unknown Error")
                    }
                }
            }
    }



    suspend fun dispatch(context: RouteContext ): DispatchResult {

        var currentContext = context

        while (true) {
            val interceptors = InterceptorManager.getInterceptorsForRequest(currentContext.request.path)

            val chain = RealRouteChain(interceptors, 0, currentContext)

            LogUtil.d("Interceptor Result : ${interceptors.first()} ${currentContext.request.path} ${currentContext.attempts} ")

            when (val action = chain.proceed(currentContext)) {

                is InterceptorResult.Continue ->  return DispatchResult.Success(currentContext)

                is InterceptorResult.Fail ->  return DispatchResult.Fail(action.reason)

                is InterceptorResult.Redirect -> {
                    if(action.newContext.attempts >= MAX_REDIRECT) return DispatchResult.Fail("Redirect Loop")
                    currentContext = action.newContext
                    LogUtil.d("Redirect Context :${currentContext.request.path} ${currentContext.attempts}")
                    continue
                }
            }
        }
    }

}