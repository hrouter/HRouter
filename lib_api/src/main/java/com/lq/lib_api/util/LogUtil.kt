package com.lq.lib_api.util



object LogUtil {
    private val logTag = "RouteLog"

    fun v(tag: String, msg: String) = println("V/$tag: $msg")
    fun i(msg: String, tag: String? = logTag) = println("I/${tag ?: logTag}: $msg")
    fun d(msg: String, tag: String? = logTag) = println("D/${tag ?: logTag}: $msg")
    fun w(tag: String, msg: String) = println("W/$tag: $msg")
    fun e(msg: String, tag: String = logTag) = println("E/$tag: $msg")
}
