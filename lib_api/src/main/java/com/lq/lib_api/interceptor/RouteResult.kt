package com.lq.lib_api.interceptor


data class RouteResult(
    val allow: Boolean, //是否允许
    val redirect: String? = null,
    val reason: String? = null
)