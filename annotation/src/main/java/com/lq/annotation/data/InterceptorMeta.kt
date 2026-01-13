package com.lq.annotation.data

data class InterceptorMeta(
    val className: String,
    val priority: Int = 0,
    val path: String = "",
)
