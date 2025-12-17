package com.lq.lib_api.entity

import com.lq.lib_api.interceptor.RouteRequest


sealed class RouteAction {
    object Continue : RouteAction()
    data class Fail(val reason: String) : RouteAction()
    data class Redirect(val newRequest: RouteRequest) : RouteAction()
    object Success : RouteAction()
}