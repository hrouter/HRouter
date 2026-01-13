package com.lq.compiler

import com.google.devtools.ksp.processing.Resolver

internal interface ProcessorPart {
    fun process(resolver: Resolver)
}
