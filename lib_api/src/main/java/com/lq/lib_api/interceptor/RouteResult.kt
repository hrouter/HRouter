package com.lq.lib_api.interceptor


data class RouteResult(
    val allow: Boolean,
    val redirect: String? = null,
    val reason: String? = null
)