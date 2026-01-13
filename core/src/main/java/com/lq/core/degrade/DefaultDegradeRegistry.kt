package com.lq.core.degrade

import com.lq.annotation.data.DegradeMeta
import com.lq.annotation.degrade.IDegradeRegister
import com.lq.core.exception.DegradeLoadException

class DefaultDegradeRegistry : IDegradeRegistry {
    private val degradeIndex = "com.lq.router.DegradeIndex" // 降级管理的路由类

    override val map: MutableMap<DegradeMeta, IRouteDegrade>

    internal var degradeClassLoader: () -> MutableMap<DegradeMeta, IRouteDegrade> = {
        mutableMapOf<DegradeMeta, IRouteDegrade>().let {
            val clazz = Class.forName(degradeIndex)
            val instance = clazz.getField("INSTANCE").get(null) // 拿到 object 的单例实例
            val method = clazz.getDeclaredMethod("getRoots")
            val registersRaw = method.invoke(instance)
            val registers =
                registersRaw as? List<IDegradeRegister>
                    ?: throw DegradeLoadException("${registersRaw?.javaClass?.name}")
            val data = mutableListOf<DegradeMeta>()
            registers.forEach {
                it.register(data)
            }
            val result = mutableMapOf<DegradeMeta, IRouteDegrade>()
            data.sortedBy { it.priority }.map {
                DegradeFactory.create(it.className).apply {
                    result[it] = this
                }
            }
            return@let result
        }
    }

    init {
        map = degradeClassLoader()
    }

    override fun getDegrades(): Map<DegradeMeta, IRouteDegrade> = map
}
