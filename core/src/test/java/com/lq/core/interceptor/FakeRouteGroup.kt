package com.lq.core.interceptor

import com.lq.annotation.data.RouteMeta
import com.lq.annotation.route.IRouteGroup
import com.lq.core.util.safeLoadPath
import kotlin.jvm.java

class FakeRouteGroup : IRouteGroup {
    override fun loadInto(groupMap: MutableMap<String, RouteMeta>) {
        safeLoadPath(groupMap, RouteMeta("/fake/fake", TestInterceptor::class.java, "fake"))
        safeLoadPath(groupMap, RouteMeta("/fake/other", TestInterceptor::class.java, "fake"))
    }
}
