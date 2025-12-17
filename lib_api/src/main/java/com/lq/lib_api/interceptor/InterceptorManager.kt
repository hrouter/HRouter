package com.lq.lib_api.interceptor

import android.content.Context
import com.lq.lib_annotation.data.InterceptorMeta
import com.lq.lib_annotation.interceptor.IInterceptorRegister
import kotlin.collections.forEach

//todo 动态参数
internal object InterceptorManager {
    val interceptors = mutableListOf<IRouteInterceptor>()

    private val data = mutableListOf<InterceptorMeta>()

    private val globalWhiteList = mutableSetOf<String>()

    /*
    * 这里是从小到大的一个TimeSort 归并排序
    * 意味着优先级值 越低 优先级越高
    * */
    fun initInterceptor(context: Context) {
        val clazz = Class.forName("com.lq.router.InterceptorIndex")
        val instance = clazz.getField("INSTANCE").get(null) // 拿到 object 的单例实例
        val method = clazz.getDeclaredMethod("getRoots")

        val registers = method.invoke(instance) as List<IInterceptorRegister>

        registers.forEach {
            it.register(data)
        }
        data.sortedBy { it.priority }.toMutableList().forEach {
            InterceptorFactory.create(context, it.className).apply {
                interceptors.add(this)
            }
        }
    }


    fun getInterceptorsForRequest(requestPath: String): List<IRouteInterceptor> {
        return interceptors.filterIndexed { index, _ ->
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

    private fun isInWhiteList(path: String, route: IRouteInterceptor): Boolean {
        return globalWhiteList.contains(path) || route.whiteList.contains(path)
    }


}