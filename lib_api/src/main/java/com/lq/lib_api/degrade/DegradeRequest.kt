package com.lq.lib_api.degrade

data class DegradeRequest(

    val newPath: String,

    val reason: String? = ""

)