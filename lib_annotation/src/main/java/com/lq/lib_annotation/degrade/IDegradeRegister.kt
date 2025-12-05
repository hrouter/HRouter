package com.lq.lib_annotation.degrade

import com.lq.lib_annotation.data.DegradeMeta

interface IDegradeRegister {

    fun register(degrades: MutableList<DegradeMeta>)
}