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
    private lateinit var routeMeta: RouteMeta
    private lateinit var intentBuilder: IntentBuilder


    /*
    * 获取路由元数据
    * */
    fun setRouteMeta(path: String): HRouterDelegate {
        routeMeta = RouteHelper.findGroup(path)
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

    private fun buildIntent(path: String): IntentBuilder {
        if (::routeMeta.isInitialized.not()) setRouteMeta(path)
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
        intentBuilder = buildIntent(path)

        val path = intentBuilder.getPath() ?: return

        val degradeContext = DegradeContext(path)

        try {

            val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + routeDegradeCoroutineHandler(degradeContext,path))
            val request = RouteRequest(path,context,bundle)

            scope.launch {
                when(val result= RouteDispatcher.dispatch(request)){
                    is DispatchResult.Success->{
                        val realPath = result.request.path
                        setRouteMeta(realPath)
                        buildIntent(realPath)
                        LogUtil.d("StartActivity: $routeMeta")
                        withContext(Dispatchers.Main){
                            startActivity()
                        }
                    }
                    is DispatchResult.Fail ->{
                        DegradeManager.handleDegrade(degradeContext,path, Exception(result.reason))
                    }
                }
            }
        }catch (e: Exception){
            DegradeManager.handleDegrade(degradeContext,path, Exception(e.message))
        }
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
        } else if (context is Activity) {
            val activity = context as Activity
            launcher?.launch(intent, options) ?: activity.startActivity(intent, options?.toBundle())
        }
    }

    private fun createOptions(): ActivityOptionsCompat? {
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