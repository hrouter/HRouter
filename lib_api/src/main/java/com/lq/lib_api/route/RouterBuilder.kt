package com.lq.lib_api.route

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.lq.lib_api.autowired.ParameterBuilder

class RouterBuilder (private val path: String){
    private val delegate = HRouterDelegate(path)
    fun withContext(ctx: Context) = apply { delegate.withContext(ctx) }

    fun withAnim(enter: Int, exit: Int) = apply { delegate.withAnim(enter, exit) }
    fun withParams(block: ParameterBuilder.() -> Unit) = apply { delegate.withParams(block) }

    fun withLauncher(launcher: ActivityResultLauncher<Intent>) = apply { delegate.withLauncher(launcher) }

    fun navigate() = delegate.navigate()
}