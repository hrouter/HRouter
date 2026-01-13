package com.lq.annotation.route

/**
 * Author: Lq
 * Date: 2025/4/30
 * Description:  IRouteRoot
 */
interface IRouteRoot {
    fun loadInto(rootMap: MutableMap<String, String>)
}
