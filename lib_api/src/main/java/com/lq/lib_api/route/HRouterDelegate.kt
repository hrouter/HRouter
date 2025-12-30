package com.lq.lib_api.route

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import com.lq.lib_api.autowired.ParameterBuilder
import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.interceptor.RouteContext
import com.lq.lib_api.interceptor.RouteRequest
import com.lq.lib_api.navigate.NavigateContext

internal class HRouterDelegate(private val path: String) {

    private val bundle = Bundle()
    private lateinit var context: Context
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var enterAnim: Int? = null
    private var exitAnim: Int? = null

    /** 链式设置参数 */
    fun withParams(block: ParameterBuilder.() -> Unit): HRouterDelegate {
        bundle.putAll(ParameterBuilder().apply(block).bundle)
        return this
    }

    fun withContext(context: Context): HRouterDelegate {
        this.context = context
        return this
    }

    fun withLauncher(launcher: ActivityResultLauncher<Intent>): HRouterDelegate {
        this.launcher = launcher
        return this
    }

    fun withAnim(enter: Int, exit: Int): HRouterDelegate {
        this.enterAnim = enter
        this.exitAnim = exit
        return this
    }

    /** 构建 Intent，仅在本地生成 */
    internal fun buildIntent(targetPath: String): IntentBuilder {
        val routeMeta = RouteHelper.findGroup(targetPath)
        return IntentBuilder(context).apply {
            set(routeMeta)
            put(bundle)
            if (context is Application) flags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /*
    * 生成navigateContext
    * */
    internal fun buildNavigateContext(degradeContext: DegradeContext?=null): NavigateContext{
        val context = NavigateContext(path,bundle,RouteContext(RouteRequest(path,bundle),0),launcher,enterAnim,exitAnim,degradeContext)
        return context
    }

    /** 仅处理 activity 启动及动画，状态局部化 */
    internal fun startActivity(intentBuilder: IntentBuilder) {
        val intent = intentBuilder.get()
        val options = createOptions()

        when (context) {
            is Application -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                launcher?.launch(intent, options) ?: context.startActivity(intent)
            }
            is Activity -> {
                val activity = context as Activity
                launcher?.launch(intent, options) ?: activity.startActivity(intent, options?.toBundle())
            }
        }
    }

    /** 构建动画选项 */
    private fun createOptions(): ActivityOptionsCompat? {
        return if (enterAnim != null && exitAnim != null && context is Activity) {
            ActivityOptionsCompat.makeCustomAnimation(context, enterAnim!!, exitAnim!!)
        } else null
    }
}
