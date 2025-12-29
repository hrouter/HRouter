package com.lq.lib_api.deeplink

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import com.lq.lib_annotation.deeplink.IDeepLinkRegister
import com.lq.lib_api.util.LogUtil
import kotlin.collections.forEach

internal object DeepLinkManager {
    private val links = mutableMapOf<String, String>()

    fun init(){
        val clazz = Class.forName("com.lq.router.DeepLinkIndex")
        val instance = clazz.getField("INSTANCE").get(null) // 拿到 object 的单例实例
        val method = clazz.getDeclaredMethod("getRoots")
        val registers = method.invoke(instance) as List<IDeepLinkRegister>

        registers.forEach {
            it.register(links)
        }
    }

    /*
    * */
    fun getPathWithNoParameters(uri:String): String?{
        val parsedUri = uri.toUri()
        val schemeHostPath = "${parsedUri.scheme}://${parsedUri.host}"
        val path = links[schemeHostPath]
        LogUtil.d("DeepLink schemeHostPath: $schemeHostPath path: $path $uri")
        if(path.isNullOrEmpty()){
            LogUtil.d("DeepLink Manager 找不到路径 path: $path")
        }
        return path
    }

    fun getPathFromUri(uri:String): String?{
        val parsedUri = uri.toUri()
        val schemeHostPath = "${parsedUri.scheme}://${parsedUri.host}${parsedUri.path ?: ""}"
        val path = links[schemeHostPath]
        LogUtil.d("DeepLink schemeHostPath: $schemeHostPath path: $path $uri")
        if(path.isNullOrEmpty()){
            LogUtil.d("DeepLink Manager 找不到路径 path: $path")
        }
       return path
    }


    /*
    * 解析deepLink的参数
    * todo 需要增加解析query
    * */
    fun getParameters(uri:String): Map<String, String> {
        val parsedUri = uri.toUri()
        val params = mutableMapOf<String, String>()
        val path = parsedUri.path
        val segments = path?.split("/")?.filter { it.isNotEmpty() } ?: emptyList()
        for (segment in segments) {
            val parts = segment.split("=")
            if (parts.size == 2) {
                params[parts[0]] = parts[1]
            }
        }
        return params
    }

    fun match(uri: Uri){

    }

    fun parseParams(uri: Uri): Map<String, String> {
        val result = mutableMapOf<String, String>()
        uri.queryParameterNames.forEach {
            result[it] = uri.getQueryParameter(it) ?: ""
        }
        return result
    }
}