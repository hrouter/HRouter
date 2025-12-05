package com.lq.lib_api

import android.app.Application
import com.lq.lib_api.autowired.AutoWiredHelper
import com.lq.lib_api.route.RouterBuilder
import com.lq.lib_api.deeplink.DeepLinkManager
import com.lq.lib_api.degrade.DegradeManager
import com.lq.lib_api.exception.UriParseIllegalException
import com.lq.lib_api.interceptor.InterceptorManager
import com.lq.lib_api.route.RouteHelper

object HRouter {

    //todo 1.支持更多参数， 2 deeplink 3 打包aar 4 注入参数的健壮性 5 loadService

    private lateinit var app: Application

    fun init(context: Application){
        app = context
        RouteHelper.findRoot()
        InterceptorManager.initInterceptor(context)
        DegradeManager.init(context)
        DeepLinkManager.init(context)
    }


    fun build(path: String): RouterBuilder {
        return RouterBuilder(path).withContext(app)
    }

    fun buildUri(uri:String): RouterBuilder {
        val path = DeepLinkManager.getPathFromUri(uri)
        if(path.isNullOrEmpty())  throw UriParseIllegalException(uri)
        return RouterBuilder(path).withContext(app)
    }

    fun inject(target: Any){
        AutoWiredHelper.inject(target)
    }

}