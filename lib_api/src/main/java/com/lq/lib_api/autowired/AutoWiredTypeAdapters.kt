package com.lq.lib_api.autowired

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import kotlin.reflect.KClass

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

    fun <T: Any> register(type: KClass<T>, adapter: (Bundle?, String) -> T?) {
        adapters[type] = adapter
    }

    fun getAdapter(type: KClass<*>): ((Bundle?, String) -> Any?)? {
        return adapters[type]
    }

}

fun test(bundle: Bundle?){
    val userName :String = AutoWiredTypeAdapters.getAdapter(String::class)?.invoke(bundle,"userName") as? String? ?:""
}