package com.lq.lib_api.interceptor

import android.content.Context
import com.lq.lib_annotation.data.InterceptorMeta
import com.lq.lib_annotation.interceptor.IInterceptorRegister
import com.lq.lib_api.util.pathMatches
import kotlin.collections.forEach

//todo 动态参数
internal object InterceptorManager {

    val interceptors = mutableMapOf<InterceptorMeta, IRouteInterceptor>()
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

        val data = mutableListOf<InterceptorMeta>()

        registers.forEach {
            it.register(data)
        }
        data.add(InterceptorMeta("com.lq.lib_api.interceptor.LogInterceptor", priority = Int.MIN_VALUE)) //先添加一个地址拦截器，打印地址，

         data.sortedBy { it.priority }.map {
            InterceptorFactory.create(context, it.className).apply {
                interceptors[it] = this
            }
        }
    }


    fun getInterceptorsForRequest(pagePath: String) =  selectInterceptors(interceptors, pagePath)

    internal fun selectInterceptors(
        source: Map<InterceptorMeta, IRouteInterceptor>,
        pagePath: String
    ): List<IRouteInterceptor> {
        return source
            .filter { pagePath.pathMatches(it.key.path) }
            .map { it.value }
    }


    private fun isInWhiteList(path: String, route: IRouteInterceptor): Boolean {
        return globalWhiteList.contains(path) || route.whiteList.contains(path)
    }


}