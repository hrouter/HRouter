package com.lq.core.degrade

class DegradeContext {
    private val visited: MutableSet<String> = mutableSetOf()

    fun markVisited(path: String): Boolean = visited.add(path)

    fun clearVisited() = visited.clear()
}
