package com.lq.lib_api.degrade

import android.content.Context
import com.lq.lib_annotation.data.DegradeMeta
import com.lq.lib_annotation.degrade.IDegradeRegister
import com.lq.lib_annotation.degrade.IRouteDegrade
import com.lq.lib_api.util.LogUtil

internal object DegradeManager {

    private val degrades = mutableSetOf<IRouteDegrade>()


    fun addDegrade(degrade: IRouteDegrade){
        degrades.add(degrade)
    }

    fun init(context: Context){
        val clazz = Class.forName("com.lq.router.DegradeIndex")
        val instance = clazz.getField("INSTANCE").get(null) // 拿到 object 的单例实例
        val method = clazz.getDeclaredMethod("getRoots")
        val registers = method.invoke(instance) as List<IDegradeRegister>
         val data = mutableListOf<DegradeMeta>()

        registers.forEach {
            it.register(data)
        }
        data.sortedBy { it.priority }.toMutableList().forEach {
            DegradeFactory.create(context,it.className).apply {
                addDegrade(this)
            }
        }
    }

    fun handleDegrade(){
        LogUtil.d("降级处理")
        for (degrade in degrades){
//            val handled = degrade.onLost(degrade,reason)
//            if(handled) return
        }
    }

}