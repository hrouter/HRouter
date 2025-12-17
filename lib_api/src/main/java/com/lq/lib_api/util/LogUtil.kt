package com.lq.lib_api.util

import android.util.Log

object LogUtil {
    private val logTag = "RouteLog"

    fun v(tag: String,msg: String){
        Log.v(tag,msg)
    }

    fun i(msg: String,tag: String ?= logTag) = Log.i(tag,msg)

    fun d(msg: String,tag:String?=logTag) = Log.d(tag,msg)

    fun w(tag:String,msg: String) = Log.w(tag,msg)

    fun e(tag:String,msg:String) = Log.e(tag,msg)
}