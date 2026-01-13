package com.lq.annotation.route

import com.lq.annotation.data.RouteMeta

interface IRouteGroup {
    fun loadInto(groupMap: MutableMap<String, RouteMeta>)
}
