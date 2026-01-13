package com.lq.compiler.wired

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lq.compiler.ProcessorPart
import com.lq.compiler.util.Const

internal class AutoWiredProcessorPart(
    environment: SymbolProcessorEnvironment,
) : ProcessorPart {
    val codeGenerator = environment.codeGenerator

    override fun process(resolver: Resolver) {
        val symbols = resolver.getSymbolsWithAnnotation(Const.AutoWiredQualifiedName)
        val classSet = symbols.mapNotNull { it.parent as? KSClassDeclaration }.distinct()
        classSet.forEach {
            it.accept(AutoWiredVisitor(codeGenerator), Unit)
        }
    }
}
