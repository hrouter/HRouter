package com.lq.lib_api.interceptor

import com.lq.lib_api.entity.DispatchResult
import com.lq.lib_api.entity.RouteAction
import com.lq.lib_api.util.LogUtil

object RouteDispatcher {
    private const val MAX_REDIRECT = 5

    suspend fun dispatch(request: RouteRequest): DispatchResult {
        var currentRequest = request
        var redirectCount = 0

        while (true) {
            LogUtil.d("打印当前action: ${currentRequest.path}")
            val interceptors = InterceptorManager.getInterceptorsForRequest(currentRequest.path)
            interceptors.forEach {
                LogUtil.d("当前interceptor:${it}")
            }
            val chain = RealRouteChain(interceptors,0, currentRequest)
            val action = chain.proceed(currentRequest)
            LogUtil.d("打印当前action: $action")
            when (action) {
                is RouteAction.Success -> {
                    LogUtil.d("SuccessPath: ${currentRequest.path}")
                    return DispatchResult.Success(currentRequest)
                }

                is RouteAction.Fail -> {
                    return DispatchResult.Fail(action.reason)
                }

                is RouteAction.Redirect -> {
                    redirectCount++
                    if (redirectCount > MAX_REDIRECT) {
                        return DispatchResult.Fail("redirect loop")
                    }
                    currentRequest = action.newRequest
                    continue
                }
                else ->{ }
            }
        }
    }



}