package com.lq.core.route

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.lq.annotation.data.RouteMeta
import com.lq.core.exception.RouteMetaIllegalException

internal class IntentBuilder(
    private val context: Context,
) {
    private val intent = Intent()
    private var routeMeta: RouteMeta? = null

    fun flags(flag: Int) {
        intent.addFlags(flag)
    }

    fun set(meta: RouteMeta?) {
        routeMeta = meta
        if (meta == null) return
        intent.setClass(context, meta.destination)
    }

    fun getPath(): String? {
        if (routeMeta == null) throw RouteMetaIllegalException()
        return routeMeta?.path
    }

    fun get() = intent

    fun put(bundle: Bundle?) {
        bundle ?: return
        intent.putExtras(bundle)
    }
}
