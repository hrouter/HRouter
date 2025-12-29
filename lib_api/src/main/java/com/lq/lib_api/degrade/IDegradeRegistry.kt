package com.lq.lib_api.degrade

import com.lq.lib_annotation.data.DegradeMeta

internal interface IDegradeRegistry {
    val map: MutableMap<DegradeMeta, IRouteDegrade>
    fun getDegrades():Map<DegradeMeta, IRouteDegrade>

}