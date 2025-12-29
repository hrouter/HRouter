package com.lq.lib_api.navigate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.interceptor.RouteContext

internal data class NavigateContext(
    val path: String,
    val bundle: Bundle? = null,
//    val context: Context,
    val routeContext: RouteContext,
    val launcher: ActivityResultLauncher<Intent>? = null,
    val enterAnim: Int? = null,
    val exitAnim: Int? = null,
    val degradeContext: DegradeContext? = null
)