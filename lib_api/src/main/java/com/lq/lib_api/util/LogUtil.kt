package com.lq.lib_api.util


internal object LogUtil {
    private val logTag = "RouteLog"

    private var debug: Boolean = false

    fun setDebugMode(isDebug: Boolean) {
        this.debug = isDebug
    }

    internal fun v(tag: String, msg: String) = debug.also { if(it) println("V/$tag: $msg") }
    internal fun i(msg: String, tag: String? = logTag) = debug.also { if(it) println("I/$tag: $msg") }
    internal fun d(msg: String, tag: String? = logTag) = debug.also { if(it) println("D/$tag: $msg") }
    internal fun w(tag: String, msg: String) = debug.also { if(it) println("W/$tag: $msg") }
    internal fun e(msg: String, tag: String = logTag) = debug.also { if(it) println("E/$tag: $msg") }
}
