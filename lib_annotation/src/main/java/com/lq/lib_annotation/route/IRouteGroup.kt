package com.lq.lib_annotation.route

import com.lq.lib_annotation.data.RouteMeta


interface IRouteGroup {

    fun loadInto(groupMap: MutableMap<String, RouteMeta>)
}
