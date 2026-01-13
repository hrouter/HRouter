package com.lq.annotation.degrade

import com.lq.annotation.data.DegradeMeta

interface IDegradeRegister {
    fun register(degrades: MutableList<DegradeMeta>)
}
