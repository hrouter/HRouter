package com.lq.compiler.util

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

fun String.capitalizeFirst(): String {
    if (isEmpty()) return this
    return replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}

fun getRootName(name: String) = "HRouterRoot${name.capitalizeFirst()}"

fun getGroupName(name: String) = "HRouterGroup${name.capitalizeFirst()}"

fun SymbolProcessorEnvironment.getModuleName(): String = this.options["HROUTER_MODULE_NAME"] ?: "default"

fun getModuleNameCapitalize(environment: SymbolProcessorEnvironment) = environment.getModuleName().capitalizeFirst()
