package com.lq.lib_api.degrade

import android.content.Context
import com.lq.lib_annotation.data.DegradeMeta
import com.lq.lib_annotation.degrade.IDegradeRegister
import com.lq.lib_api.HRouter
import com.lq.lib_api.degrade.IRouteDegrade
import com.lq.lib_api.entity.DegradeResult
import com.lq.lib_api.interceptor.InterceptorManager.interceptors
import com.lq.lib_api.util.LogUtil
import com.lq.lib_api.util.pathMatches

internal object DegradeManager {

    private val degrades = mutableMapOf<DegradeMeta, IRouteDegrade>()

    private val degradeContext = DegradeContext()

    fun init(context: Context) {
        val clazz = Class.forName("com.lq.router.DegradeIndex")
        val instance = clazz.getField("INSTANCE").get(null) // 拿到 object 的单例实例
        val method = clazz.getDeclaredMethod("getRoots")
        val registers = method.invoke(instance) as List<IDegradeRegister>
        val data = mutableListOf<DegradeMeta>()
        registers.forEach {
            it.register(data)
        }
        data.sortedBy { it.priority }.map {
            DegradeFactory.create(context, it.className).apply {
                degrades[it] = this
            }
        }

    }

    fun handleDegrade(path: String, exception: Throwable) {
        LogUtil.i("Degrade Handler :${path} ${exception.message}")

        if (!degradeContext.markVisited(path)) {
            LogUtil.i("Degrade Handler Loop :${path}")
            degradeContext.clearVisited()
            return
        }

        val degradeRequest = DegradeRequest(path, "${exception.message}")

        try {
            for (degrade in getDegradesFromRequest(path)) {
                when (val result = degrade.onLost(degradeRequest)) {
                    is DegradeResult.Redirect -> {
                        HRouter.build(result.newRequest.newPath).navigate()
                    }

                    is DegradeResult.Ignore -> {
                        LogUtil.i("Degrade Handler Ignore :${path}")
                    }
                }
            }
        } catch (e: Exception) {
            degradeContext.clearVisited()
            LogUtil.e("Degrade Handler Exception : ${e.message}")
        }
    }


    fun getDegradesFromRequest(requestPath: String): List<IRouteDegrade> =  degrades.filter { requestPath.pathMatches(it.key.path) }.map { it.value }


}