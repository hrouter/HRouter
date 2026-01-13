package com.lq.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.lq.compiler.deeplink.RouteDeepLinkPart
import com.lq.compiler.degrade.RouteDegradePart
import com.lq.compiler.interceptor.RouteInterceptorPart
import com.lq.compiler.route.RouteProcessorPart
import com.lq.compiler.wired.AutoWiredProcessorPart

internal class HRouterSymbolProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val processors =
            listOf(
                AutoWiredProcessorPart(environment),
                RouteProcessorPart(environment),
                RouteInterceptorPart(environment),
                RouteDegradePart(environment),
                RouteDeepLinkPart(environment),
            )
        processors.forEach {
            it.process(resolver)
        }
        return emptyList()
    }
}
