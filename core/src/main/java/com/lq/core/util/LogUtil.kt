package com.lq.core.util

internal object LogUtil {
    private const val LOG_TAG = "RouteLog"

    private var debug: Boolean = false

    fun setDebugMode(isDebug: Boolean) {
        this.debug = isDebug
    }

    internal fun v(
        tag: String,
        msg: String,
    ) = debug.also { if (it) println("V/$tag: $msg") }

    internal fun i(
        msg: String,
        tag: String? = LOG_TAG,
    ) = debug.also { if (it) println("I/$tag: $msg") }

    internal fun d(
        msg: String,
        tag: String? = LOG_TAG,
    ) = debug.also { if (it) println("D/$tag: $msg") }

    internal fun w(
        tag: String,
        msg: String,
    ) = debug.also { if (it) println("W/$tag: $msg") }

    internal fun e(
        msg: String,
        tag: String = LOG_TAG,
    ) = debug.also { if (it) println("E/$tag: $msg") }
}
