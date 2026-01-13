package com.lq.core.route

import com.lq.annotation.data.RouteMeta
import com.lq.annotation.route.IRouteGroup
import com.lq.annotation.route.IRouteRoot
import com.lq.core.exception.GroupNotFoundException
import com.lq.core.exception.PathIllegalException
import com.lq.core.exception.PathNotFoundException
import com.lq.core.util.LogUtil
import com.lq.core.util.isValidateRoute
import java.util.concurrent.ConcurrentHashMap

internal object RouteHelper {
    private val routeRoot = mutableMapOf<String, String>()
    private val groupCache = ConcurrentHashMap<String, Map<String, RouteMeta>>()

    internal var routeRootProvider: () -> MutableMap<String, String> = {
        mutableMapOf<String, String>().also { map ->
            val clazz = Class.forName("com.lq.router.HRouterIndex")
            val instance = clazz.getField("INSTANCE").get(null)
            val method = clazz.getDeclaredMethod("getRoots")
            val roots = method.invoke(instance) as List<IRouteRoot>
            roots.forEach { it.loadInto(map) }
        }
    }

    internal var groupLoader: (String) -> IRouteGroup = { path ->
        Class
            .forName(path)
            .getDeclaredConstructor()
            .newInstance() as IRouteGroup
    }

    fun init() {
        initWithRouteRoots(routeRootProvider())
    }

    /** 框架内部用：注入路由表 */
    internal fun initWithRouteRoots(routeRoots: Map<String, String>) {
        routeRoot.clear()
        routeRoot.putAll(routeRoots)
    }

    /*
     * 根据路由地址获取分组信息,若缓存命中直接取，非命中反射加载
     * todo 后续版本需要增加并发处理
     * */
    fun findGroup(path: String): RouteMeta {
        val segments = path.split("/")
        require(isValidateRoute(path)) { throw PathIllegalException(path) }

        val groupMap = getGroupFromCache(path)
        val routeMeta = groupMap?.get(path)
        if (routeMeta != null) return routeMeta

        val group = segments[1]
        val groupPath = routeRoot[group] ?: throw GroupNotFoundException(group)
        return getGroup(groupPath, path)
    }

    /*
     * 从反射中获取数据并缓存
     * */
    private fun getGroup(
        groupPath: String,
        path: String,
    ): RouteMeta {
        val groupMap = groupCache[groupPath]?.toMutableMap() ?: mutableMapOf()
        val groupInfo = groupLoader(groupPath)
        groupInfo.loadInto(groupMap)
        LogUtil.d("groupPath:$groupPath path:$path groupInfo:$groupInfo ")
        val routeMeta = groupMap[path] ?: throw PathNotFoundException(path)
        cacheGroup(path, groupMap)
        return routeMeta
    }

    /*
     * 从缓存中取出分组信息
     * */
    private fun getGroupFromCache(group: String): Map<String, RouteMeta>? = groupCache[group]

    /*
     * 缓存分组信息
     * */
    private fun cacheGroup(
        group: String,
        map: Map<String, RouteMeta>,
    ) {
        groupCache.putIfAbsent(group, map)
    }
}
