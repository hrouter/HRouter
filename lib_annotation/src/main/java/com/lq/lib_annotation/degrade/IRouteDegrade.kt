package com.lq.lib_annotation.degrade

interface IRouteDegrade {

    fun onLost(path:String,reason:String): Boolean
}