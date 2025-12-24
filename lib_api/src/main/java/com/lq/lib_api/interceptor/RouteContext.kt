package com.lq.lib_api.interceptor

data class RouteContext(
    val request: RouteRequest,
    val attempts:Int,
) {

    fun redirect(newPath: String): RouteRequest{
        return request.copy(path = newPath)
    }

    fun attemptsAdd(){
    }



    private fun updateAttempt(){

    }

}