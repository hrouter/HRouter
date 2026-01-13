package com.lq.core.interceptor

import android.os.Bundle

data class RouteRequest(
    val path: String,
    val extras: Bundle? = null,
)
