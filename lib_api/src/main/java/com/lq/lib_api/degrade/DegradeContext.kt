package com.lq.lib_api.degrade

class DegradeContext() {

    private val visited: MutableSet<String> = mutableSetOf()

    fun markVisited(path: String):Boolean{
        return visited.add(path)
    }

    fun clearVisited() = visited.clear()
}