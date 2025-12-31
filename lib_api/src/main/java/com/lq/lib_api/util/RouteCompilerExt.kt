package com.lq.lib_api.util

import com.lq.lib_annotation.data.RouteMeta
import com.lq.lib_api.exception.RouteConflictException


/**
 * @param path 检查路由地址是否合理
 * */
internal fun isValidateRoute(path: String): Boolean {
    val routeRegex = Regex("^/[a-zA-Z0-9_-]+(/[a-zA-Z0-9_-]+)+\$")
    return routeRegex.matches(path)
}


/**
 * @param groupMap 分组Map，用于检测路由地址冲突
 * 暂时没什么太多的用处
 **/
 fun safeLoadPath(groupMap: MutableMap<String, RouteMeta>, routeMeta: RouteMeta) {
    val old = groupMap.putIfAbsent(routeMeta.path, routeMeta)
    if (old != null) throw RouteConflictException(
        routeMeta.path,
        old.destination,
        routeMeta.destination
    )
}