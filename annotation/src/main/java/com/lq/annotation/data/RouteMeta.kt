package com.lq.annotation.data

/**
 * Author: Lq
 * Date: 2025/5/5
 * Description:  RouteMeta
 */
data class RouteMeta(
    val path: String,
    val destination: Class<*>,
    val group: String,
)
