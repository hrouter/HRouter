package com.lq.lib_api.util

import com.lq.lib_annotation.data.RouteMeta
import com.lq.lib_api.exception.RouteConflictException


fun safeLoadPath(groupMap: MutableMap<String, RouteMeta>, path: String, routeMeta: RouteMeta) {
    val old = groupMap.putIfAbsent(routeMeta.path, routeMeta)
    LogUtil.d("safeLoadPath :${routeMeta.path} $routeMeta $old")
    if (old != null) throw RouteConflictException(routeMeta.path, old.destination, routeMeta.destination)
}