package com.lq.core.degrade

import com.lq.annotation.data.DegradeMeta

internal interface IDegradeRegistry {
    val map: MutableMap<DegradeMeta, IRouteDegrade>

    fun getDegrades(): Map<DegradeMeta, IRouteDegrade>
}
