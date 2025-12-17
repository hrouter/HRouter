package com.lq.lib_api.degrade

import android.content.Context
import com.lq.lib_annotation.data.DegradeMeta
import com.lq.lib_annotation.degrade.IDegradeRegister
import com.lq.lib_annotation.degrade.IRouteDegrade
import com.lq.lib_api.util.LogUtil

internal object DegradeManager {

    private val degrades = mutableListOf<IRouteDegrade>()

    private val data = mutableListOf<DegradeMeta>()

    fun addDegrade(degrade: IRouteDegrade) {
        degrades.add(degrade)
    }

    fun init(context: Context) {
        val clazz = Class.forName("com.lq.router.DegradeIndex")
        val instance = clazz.getField("INSTANCE").get(null) // 拿到 object 的单例实例
        val method = clazz.getDeclaredMethod("getRoots")
        val registers = method.invoke(instance) as List<IDegradeRegister>

        registers.forEach {
            it.register(data)
        }
        val sortedDegrades = data
            .sortedBy { it.priority }  // 按 priority 升序（数值小的先执行）
            .map { DegradeFactory.create(context, it.className) }

        degrades.clear()
        degrades.addAll(sortedDegrades)
    }

    fun handleDegrade(context: DegradeContext,path:String, exception: Throwable) {
        if(!context.markVisited(path)) {
            LogUtil.i("Degrade Handler Loop :${path}")
            return
        }
        try {
            for (degrade in getInterceptorsForRequest(path)) {
                val handled = degrade.onLost(path, "${exception.message}")
                if (handled) return
            }
        } catch (e: Exception) {
            LogUtil.e("DegradeException", "Degrade Handler Exception : ${e.message}")
        }
    }


    fun getInterceptorsForRequest(requestPath: String): List<IRouteDegrade> {
        return degrades.filterIndexed { index, _ ->
            val meta = data.getOrNull(index) ?: return@filterIndexed false
            pathMatches(requestPath, meta.path)
        }
    }

    private fun pathMatches(requestPath: String, interceptorPath: String): Boolean {
        if (interceptorPath == requestPath) return true
        if (interceptorPath.endsWith("/*") &&
            requestPath.startsWith(interceptorPath.removeSuffix("/*"))
        ) return true
        if (interceptorPath == "*") return true
        if (interceptorPath == "") return true
        return false
    }

}