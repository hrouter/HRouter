package com.lq.lib_api.util

import com.lq.lib_api.interceptor.IRouteInterceptor


fun IRouteInterceptor.pathMatches(requestPath: String): Boolean {

    return false
}