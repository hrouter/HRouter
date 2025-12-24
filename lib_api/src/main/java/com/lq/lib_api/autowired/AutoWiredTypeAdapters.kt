package com.lq.lib_api.autowired

import android.os.Bundle
import android.os.Parcelable
import com.lq.lib_api.util.LogUtil
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * HRouter AutoWired 规则：
 * 1. 基本类型、数组：直接支持
 * 2. Parcelable 子类：统一走 Parcelable
 * 3. Serializable 子类：统一走 Serializable
 * 4. 其余类型：直接失败
 *
 * 不支持自定义 register，以避免运行期不可控行为
 */
object AutoWiredTypeAdapters {


    private val adapters = mutableMapOf<KClass<*>, (Bundle?, String) -> Any?>()

    init {
        // 基本类型
        adapters[String::class] = { b, k -> b?.getString(k) }
        adapters[Int::class] = { b, k -> b?.getInt(k) }
        adapters[Boolean::class] = { b, k -> b?.getBoolean(k) }
        adapters[Float::class] = { b, k -> b?.getFloat(k) }
        adapters[Double::class] = { b, k -> b?.getDouble(k) }
        adapters[Long::class] = { b, k -> b?.getLong(k) }
        adapters[Short::class] = { b, k -> b?.getShort(k) }

        // 数组类型
        adapters[FloatArray::class] = { b, k -> b?.getFloatArray(k) }
        adapters[IntArray::class] = { b, k -> b?.getIntArray(k) }
        adapters[LongArray::class] = { b, k -> b?.getLongArray(k) }
        adapters[DoubleArray::class] = { b, k -> b?.getDoubleArray(k) }
        adapters[ShortArray::class] = { b, k -> b?.getShortArray(k) }
        adapters[BooleanArray::class] = { b, k -> b?.getBooleanArray(k) }
        adapters[CharArray::class] = { b, k -> b?.getCharArray(k) }

        // 可序列化类型
        adapters[Parcelable::class] = { b, k -> b?.getParcelable(k) }
        adapters[Serializable::class] = { b, k -> b?.getSerializable(k) }
    }


    /*fun <T: Any> register(type: KClass<T>, adapter: (Bundle?, String) -> T?) {
        adapters[type] = adapter
    }*/

    fun getAdapter(type: KClass<*>): ((Bundle?, String) -> Any?)? {
        if (Parcelable::class.java.isAssignableFrom(type.java)) {
            return adapters[Parcelable::class]
        }
        if(Serializable::class.java.isAssignableFrom(type.java)){
            return adapters[Serializable::class]
        }
        return adapters[type]
    }

}

fun test(bundle: Bundle?){
}