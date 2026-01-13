package com.lq.core.autowired

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lq.core.exception.BundleEmptyException
import com.lq.core.util.LogUtil
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

internal object AutoWiredHelper {
    private val cache = ConcurrentHashMap<String, Pair<Any, Method>>()

    fun inject(target: Any) {
        val className = target::class.java
        val injectClassName = "${className.name}AutoWired"
        LogUtil.d("className:$className  injectClass:$injectClassName")

        val (injectorInstance, injectMethod) =
            cache[injectClassName] ?: run {
                val clazz = Class.forName(injectClassName)
                val instance = clazz.getDeclaredConstructor().newInstance()
                val method = clazz.getDeclaredMethod("inject", target::class.java, Bundle::class.java)
                val pair = instance to method
                cache[className.name] = pair
                pair
            }
        val bundle =
            when (target) {
                is Activity -> target.intent?.extras
                is Fragment -> target.arguments
                else -> null
            } ?: throw BundleEmptyException("$className")
        injectMethod.invoke(injectorInstance, target, bundle)
    }
}
