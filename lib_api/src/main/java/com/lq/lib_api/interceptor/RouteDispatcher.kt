package com.lq.lib_api.interceptor

import com.lq.lib_api.entity.DispatchResult
import com.lq.lib_api.entity.RouteAction

object RouteDispatcher {
    private const val MAX_REDIRECT = 5

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