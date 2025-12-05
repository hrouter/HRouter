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
import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.interceptor.InterceptorHistory
import com.lq.lib_api.interceptor.InterceptorManager
import com.lq.lib_api.interceptor.RouteDispatcher
import com.lq.lib_api.interceptor.RouteRequest
import com.lq.lib_api.interceptor.RouteResult
import com.lq.lib_api.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

internal class HRouterDelegate(path: String) {
    private var realPath = path
    private val bundle = Bundle()
    private lateinit var context: Context
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var enterAnim: Int? = null
    private var exitAnim: Int? = null

    private var isRedirect : AtomicBoolean = AtomicBoolean(false)
    private lateinit var routeMeta: RouteMeta
    private lateinit var intentBuilder : IntentBuilder


    /*
    * 获取路由元数据
    * */
    fun setRouteMeta(): HRouterDelegate{
        routeMeta = RouteHelper.findGroup(realPath)
        return this
    }

    fun setRedirect(redirect : Boolean = true): HRouterDelegate{
        isRedirect.set(redirect)
        return this
    }

    fun setPath(newPath: String): HRouterDelegate {
        realPath = newPath
        return this
    }

    fun withParams(block: ParameterBuilder.() -> Unit): HRouterDelegate {
        bundle.putAll(ParameterBuilder().apply(block).bundle)
        return this
    }

    fun withLauncher(launcher: ActivityResultLauncher<Intent>): HRouterDelegate {
        this.launcher = launcher
        return this
    }

    fun withContext(context: Context): HRouterDelegate {
        this.context = context
        return this
    }

    fun withAnim(enter: Int, exit: Int): HRouterDelegate {
        this.enterAnim = enter
        this.exitAnim = exit
        return this
    }

    private fun buildIntent(): IntentBuilder {
        if(::routeMeta.isInitialized.not()) setRouteMeta()
        return IntentBuilder(context).apply {
            set(routeMeta)
            put(bundle)
            if (context is Application) flags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }


    /*
    * 如果是重定向 直接启动activity，不进行拦截，降级
    * todo 需测试
    * */
    fun navigate() {
        try {
            intentBuilder = buildIntent()
            val path = intentBuilder.getPath() ?: return
            val request = RouteRequest(path, context, bundle)
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch{
                val result = RouteDispatcher.dispatch(request)
                withContext(Dispatchers.Main){
                    consumeResult(result)
                }

            }
            InterceptorHistory.pop(path)

        }catch (e: Exception){
            LogUtil.d("navigate exception : ${e.message}")
            DegradeManager.handleDegrade()
        }
    }

    fun consumeResult(result: RouteResult) {
        if (result.allow && result.redirect == null) {
            // 放行 — 正常启动
            startActivity()
            return
        }

        if (result.redirect != null) {
            // 重定向
            LogUtil.d("重定向")
            return
        }
        LogUtil.d("拦截成功")

    }


    /*
    * context 是Activity 且自带动画时，取消系统动画
    * context 是 application 时，此时启动另一个activity需要添加新栈
    * todo launcher和动画 不冲突
    *  */
    private fun startActivity() {
        val intent = intentBuilder.get()
        val options = createOptions()

        if (context is Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launcher?.launch(intent, options) ?: context.startActivity(intent)
        }

        else if (context is Activity) {
            val activity = context as Activity
            launcher?.launch(intent, options) ?: activity.startActivity(intent, options?.toBundle())
        }
    }

    private fun createOptions() : ActivityOptionsCompat? {
        return if (enterAnim != null && exitAnim != null && context is Activity) {
            ActivityOptionsCompat.makeCustomAnimation(
                context,
                enterAnim!!,
                exitAnim!!
            )
        } else {
            null
        }
    }


}