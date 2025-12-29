package com.lq.lib_api.degrade


internal object DegradeFactory {

    private val interceptorCache = mutableMapOf<String, IRouteDegrade>()

    //todo 拓展需要context 对降级进行初始化，比如从sp或者dataStore中获取数据，诸如此类
    fun create(className: String): IRouteDegrade {
        return interceptorCache.getOrPut(className) {
           Class.forName(className).getDeclaredConstructor().newInstance() as IRouteDegrade
        }
    }
}