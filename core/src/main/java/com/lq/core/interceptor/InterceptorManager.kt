package com.lq.core.interceptor

import android.app.Application
import com.lq.annotation.data.InterceptorMeta
import com.lq.annotation.interceptor.IInterceptorRegister
import com.lq.core.util.pathMatches

// todo 动态参数
internal object InterceptorManager {
    val interceptors = mutableMapOf<InterceptorMeta, IRouteInterceptor>()

    /*
     * 这里是从小到大的一个TimeSort 归并排序
     * 意味着优先级的值 越低 优先级越高
     * */
    fun initInterceptor(context: Application) {
        val clazz = Class.forName("com.lq.router.InterceptorIndex")
        val instance = clazz.getField("INSTANCE").get(null) // 拿到 object 的单例实例
        val method = clazz.getDeclaredMethod("getRoots")

        val registers = method.invoke(instance) as List<IInterceptorRegister>

        val data = mutableListOf<InterceptorMeta>()

        registers.forEach {
            it.register(data)
        }
//        data.add(InterceptorMeta("com.lq.lib_api.interceptor.LogInterceptor", priority = Int.MIN_VALUE)) //先添加一个地址拦截器，打印地址，

        data.sortedBy { it.priority }.map {
            InterceptorFactory.create(context, it.className).apply {
                interceptors[it] = this
            }
        }
    }

    fun getInterceptorsForRequest(pagePath: String) = selectInterceptors(interceptors, pagePath)

    internal fun selectInterceptors(
        source: Map<InterceptorMeta, IRouteInterceptor>,
        pagePath: String,
    ): List<IRouteInterceptor> =
        source
            .filter { pagePath.pathMatches(it.key.path) }
            .map { it.value }
}
