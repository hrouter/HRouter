package com.lq.lib_api.degrade

class DegradeContext(originPath:String) {

    private var currentPath:String = originPath
    private val visited: MutableSet<String> = mutableSetOf()

    fun updateCurrentPath(path:String){
        currentPath = path
    }

    fun markVisited(path: String):Boolean{
        return visited.add(path)
    }
}