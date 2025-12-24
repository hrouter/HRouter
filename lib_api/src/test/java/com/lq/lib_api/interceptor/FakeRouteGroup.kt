package com.lq.lib_api.interceptor

import com.lq.lib_annotation.data.RouteMeta
import com.lq.lib_annotation.route.IRouteGroup
import com.lq.lib_api.util.safeLoadPath
import kotlin.jvm.java

class FakeRouteGroup : IRouteGroup {
    override fun loadInto(groupMap: MutableMap<String, RouteMeta>) {
        safeLoadPath(groupMap,RouteMeta("/fake/fake", TestInterceptor::class.java, "fake"))
    }
}