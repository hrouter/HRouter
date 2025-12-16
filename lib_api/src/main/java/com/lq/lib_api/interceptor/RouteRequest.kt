package com.lq.lib_api.interceptor

import android.content.Context
import android.os.Bundle

data class RouteRequest(
    val path: String,
    val context: Context,
    val extras: Bundle? = null,
)