package com.lq.lib_api.degrade

import android.util.Log
import com.lq.lib_annotation.data.DegradeMeta
import com.lq.lib_api.HRouter
import com.lq.lib_api.entity.DegradeResult
import com.lq.lib_api.util.LogUtil
import com.lq.lib_api.util.pathMatches

internal object DegradeManager {

    private lateinit var registry: IDegradeRegistry

    fun setRegistry(registry: IDegradeRegistry){
        this.registry = registry
    }

    fun init() {
        setRegistry(DefaultDegradeRegistry())
    }

    /*
    * 一般来说降级不携带参数，避免污染
    * */
    fun handleDegrade(path: String, exception: Throwable, context: DegradeContext? = null) {
        val degradeContext = context ?: DegradeContext()
        LogUtil.d("Degrade handle degrade :$path context :$context")
        if (!degradeContext.markVisited(path)) {
            degradeContext.clearVisited()
            LogUtil.d("Degrade handle loop :$path")
            return
        }

        val degrades = getDegradesFromRequest(registry.getDegrades(),path)
        if (degrades.isEmpty()) return

        processDegrades(path, exception, degradeContext, degrades)
    }

    private fun processDegrades(
        path: String,
        exception: Throwable,
        context: DegradeContext,
        degrades: List<IRouteDegrade>
    ) {
        val request = DegradeRequest(path, exception.message ?: "Unknown error")

        degrades.forEach { degrade ->
            runCatching { degrade.onLost(request) }
                .onSuccess { handleResult(request,it, context) }
                .onFailure {
                    context.clearVisited()
                    LogUtil.e("Degrade Handler Exception : ${it.message}")
                }
        }
    }


    private fun handleResult(request: DegradeRequest, result: DegradeResult, context: DegradeContext) {
        LogUtil.d("handleResult:$result")
        when (result) {
            is DegradeResult.Ignore -> LogUtil.i("Degrade Handler Ignore :${request.newPath}")
            is DegradeResult.Redirect -> HRouter.build(result.newRequest.newPath).navigateWithDegradeContext(context)
        }
    }

    /*
    * 查找匹配的降级器，并按优先级排序
    * */
    val getDegradesFromRequest :(map:Map<DegradeMeta, IRouteDegrade>, requestPath: String) -> List<IRouteDegrade> = { map,path->
        map.filter { path.pathMatches(it.key.path) }
            .toList()
            .sortedBy { it.first.priority }
            .map { it.second }
    }

}