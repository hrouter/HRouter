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
    fun initInterceptor(context: Context){
        val clazz = Class.forName("com.lq.router.InterceptorIndex")
        val instance = clazz.getField("INSTANCE").get(null) // 拿到 object 的单例实例
        val method = clazz.getDeclaredMethod("getRoots")

        val registers = method.invoke(instance) as List<IInterceptorRegister>

        registers.forEach {
           it.register(data)
        }
        data.sortedBy { it.priority }.toMutableList().forEach {
            InterceptorFactory.create(context,it.className).apply {
                addInterceptor(this)
            }
        }
    }


    fun addInterceptor(intercept: IRouteInterceptor){
        interceptors.add(intercept)
    }


    private fun isFromGroup(path:String,group:String):Boolean{
        return true
    }

    private fun isInWhiteList(path: String,route: IRouteInterceptor) :Boolean{
        return globalWhiteList.contains(path) || route.whiteList.contains(path)
    }


}