package com.lq.annotation.interceptor

import com.lq.annotation.data.InterceptorMeta

interface IInterceptorRegister {
    fun register(interceptors: MutableList<InterceptorMeta>)
}
