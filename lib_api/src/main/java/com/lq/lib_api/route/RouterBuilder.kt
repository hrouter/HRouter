package com.lq.lib_api.route

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.lq.lib_api.autowired.ParameterBuilder
import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.navigate.DefaultNavigateExecutor
import com.lq.lib_api.navigate.NavigateContext
import com.lq.lib_api.navigate.NavigateExecutor

class RouterBuilder (val path: String){
    private val delegate = HRouterDelegate(path)
    private var executor : NavigateExecutor = DefaultNavigateExecutor()
    fun withContext(ctx: Context) = apply { delegate.withContext(ctx) }

    fun withAnim(enter: Int, exit: Int) = apply { delegate.withAnim(enter, exit) }
    fun withParams(block: ParameterBuilder.() -> Unit) = apply { delegate.withParams(block) }

    fun withLauncher(launcher: ActivityResultLauncher<Intent>) = apply { delegate.withLauncher(launcher) }

    fun navigate() {
      navigateExecute()
    }

    internal fun navigateWithDegradeContext(degradeContext: DegradeContext){
        navigateExecute(degradeContext)
    }

    private fun navigateExecute(degradeContext: DegradeContext?=null){
        val navigateContext: NavigateContext = delegate.buildNavigateContext(degradeContext)
        executor.execute(delegate, navigateContext)
    }
}