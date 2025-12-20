package com.lq.lib_api.interceptor

import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.entity.DispatchResult
import com.lq.lib_api.entity.RouteAction
import com.lq.lib_api.util.routeDegradeCoroutineHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object RouteDispatcher {
    private const val MAX_REDIRECT = 5


    /**
     * 异步调度路由请求
     * @param request 路由请求对象
     * @param onSuccess 成功回调，返回最终路径
     * @param onFail 失败回调，返回失败原因
     */
    fun dispatchAsync(
        request: RouteRequest,
        onSuccess: (realPath: String) -> Unit,
        onFail: (reason: String) -> Unit
    ) {

        // 使用协程在 IO 线程调度
        CoroutineScope(Dispatchers.IO + SupervisorJob() + routeDegradeCoroutineHandler(request.path))
            .launch {
                try {
                    when (val result = dispatch(request)) {
                        is DispatchResult.Success -> {
                            val realPath = result.request.path
                            withContext(Dispatchers.Main) {
                                onSuccess(realPath)
                            }
                        }

                        is DispatchResult.Fail -> {
                            withContext(Dispatchers.Main) {
                                onFail(result.reason)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onFail(e.message ?: "Unknown Error")
                    }
                }
            }
    }



    suspend fun dispatch(request: RouteRequest): DispatchResult {

        var currentRequest = request

        var redirectCount = 0

        while (true) {
            val interceptors = InterceptorManager.getInterceptorsForRequest(currentRequest.path)

            val chain = RealRouteChain(interceptors, 0, currentRequest)

            when (val action = chain.proceed(currentRequest)) {

                is RouteAction.Success ->  return DispatchResult.Success(currentRequest)

                is RouteAction.Fail ->  return DispatchResult.Fail(action.reason)

                is RouteAction.Redirect -> {
                    if(redirectCount >= MAX_REDIRECT) return DispatchResult.Fail("redirect loop")
                    redirectCount++
                    currentRequest = action.newRequest
                    continue
                }

                else -> {}
            }
        }
    }

}