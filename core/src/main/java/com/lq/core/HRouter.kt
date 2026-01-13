package com.lq.core

import android.app.Application
import com.lq.core.autowired.AutoWiredHelper
import com.lq.core.deeplink.DeepLinkManager
import com.lq.core.degrade.DegradeManager
import com.lq.core.exception.UriParseIllegalException
import com.lq.core.interceptor.InterceptorManager
import com.lq.core.route.RouteHelper
import com.lq.core.route.RouterBuilder
import com.lq.core.util.LogUtil

object HRouter {
    // todo  1 deeplink参数解析 2.@Route路由模板化 3 打包aar  4 loadService 5 fragmentManager

    private lateinit var app: Application

    fun init(context: Application) {
        app = context
        RouteHelper.init()
        InterceptorManager.initInterceptor(context)
        DegradeManager.init()
        DeepLinkManager.init()
    }

    /*
     * 进行跳转
     * */
    fun build(path: String): RouterBuilder {
        val builder = RouterBuilder(path)
        if (::app.isInitialized) builder.withContext(app)
        return builder
    }

    /*
     * low 版，deepLink解析，解析参数尚待完善
     * */
    fun buildUriWithNoParameters(uri: String): RouterBuilder {
        val path = DeepLinkManager.getPathWithNoParameters(uri)
        if (path.isNullOrEmpty()) throw UriParseIllegalException(uri)
        val routerBuilder = RouterBuilder(path).withContext(app)
        return routerBuilder
    }

    /*
     * 正常解析deeplink
     * todo 这里必须优先重构@Route注解 使其模板化
     * */
    fun buildUri(uri: String): RouterBuilder {
        val path = DeepLinkManager.getPathFromUri(uri)
        if (path.isNullOrEmpty()) throw UriParseIllegalException(uri)
        val routerBuilder = RouterBuilder(path).withContext(app)
        val uriParameters = DeepLinkManager.getParameters(uri)
        uriParameters.forEach { (key, value) ->
            routerBuilder.withParams {
                key to value
            }
        }
        return routerBuilder
    }

    fun debug(isDebug: Boolean) {
        LogUtil.setDebugMode(isDebug)
    }

    /**
     * 自动注入数据
     * */
    fun inject(target: Any) {
        AutoWiredHelper.inject(target)
    }
}
