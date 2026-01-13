package com.lq.core.exception

import com.lq.annotation.data.DegradeMeta
import com.lq.core.degrade.IDegradeRegistry
import com.lq.core.degrade.IRouteDegrade

class FakeDegradeRegistry : IDegradeRegistry {
    override val map: MutableMap<DegradeMeta, IRouteDegrade> = mutableMapOf()

    fun setDegrades(degradeMap: Map<DegradeMeta, IRouteDegrade>) {
        map.clear()
        map.putAll(degradeMap)
    }

    override fun getDegrades(): Map<DegradeMeta, IRouteDegrade> = map
}
