package com.lq.core.degrade

data class DegradeRequest(
    val newPath: String,
    val reason: String? = "",
)
