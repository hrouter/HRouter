package com.lq.lib_api.route

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import com.lq.lib_annotation.data.RouteMeta
import com.lq.lib_api.autowired.ParameterBuilder
import com.lq.lib_api.degrade.DegradeContext
import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.entity.DispatchResult
import com.lq.lib_api.interceptor.RouteDispatcher
import com.lq.lib_api.interceptor.RouteRequest
import com.lq.lib_api.util.LogUtil
import com.lq.lib_api.util.routeDegradeCoroutineHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private fun buildIntent(targetPath: String): IntentBuilder {
        val routeMeta = RouteHelper.findGroup(targetPath)
        return IntentBuilder(context).apply {
            set(routeMeta)
            put(bundle)
            if (context is Application) flags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /** 启动流程，负责 dispatch 调度和最终跳转 */
    fun navigate() {
        val request = RouteRequest(path, context, bundle)
        val degradeContext = DegradeContext(path)

        RouteDispatcher.dispatchAsync(request,
            onSuccess = { realPath ->
                val intentBuilder = buildIntent(realPath)
                startActivity(intentBuilder)
            },
            onFail = { reason ->
                DegradeManager.handleDegrade(degradeContext, path, Exception(reason))
            }
        )
    }

    /** 仅处理 activity 启动及动画，状态局部化 */
    private fun startActivity(intentBuilder: IntentBuilder) {
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
