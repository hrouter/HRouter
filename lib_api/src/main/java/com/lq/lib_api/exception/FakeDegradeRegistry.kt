package com.lq.lib_api.exception

import com.lq.lib_annotation.data.DegradeMeta
import com.lq.lib_api.degrade.IDegradeRegistry
import com.lq.lib_api.degrade.IRouteDegrade

class FakeDegradeRegistry : IDegradeRegistry {

    override val map: MutableMap<DegradeMeta, IRouteDegrade> = mutableMapOf()

    fun setDegrades(degradeMap: Map<DegradeMeta, IRouteDegrade>) {
        map.clear()
        map.putAll(degradeMap)
    }

    override fun getDegrades(): Map<DegradeMeta, IRouteDegrade> {
        return map
    }
}