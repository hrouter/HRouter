package com.lq.lib_api.interceptor

import java.util.Collections

/*
* 拦截器历史记录
* 需要避免循环跳转，管理跳转历史，返回栈
* */
internal object InterceptorHistory {
    private val pathStack = Collections.synchronizedSet(LinkedHashSet<String>())

    private const val MAX_STACK = 4

    fun push(path:String):Boolean {
        if(pathStack.contains(path)) {
            return false
        }
        pathStack.add(path)
        return true
    }

    fun pop(path: String){
        pathStack.remove(path)
    }

    fun hasVisited(path:String):Boolean  = pathStack.contains(path)

    fun clear() = pathStack.clear()

}