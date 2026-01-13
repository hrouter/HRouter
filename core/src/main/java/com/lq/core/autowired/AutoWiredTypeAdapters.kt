package com.lq.core.autowired

import android.os.Bundle
import android.os.Parcelable
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
        adapters[Int::class] = { b, k -> b?.getInt(k) }
        adapters[String::class] = { b, k -> b?.getString(k) }
        adapters[Boolean::class] = { b, k -> b?.getBoolean(k) }
        adapters[Float::class] = { b, k -> b?.getFloat(k) }
        adapters[Double::class] = { b, k -> b?.getDouble(k) }
        adapters[Long::class] = { b, k -> b?.getLong(k) }
        adapters[Short::class] = { b, k -> b?.getShort(k) }
        adapters[Byte::class] = { b, k -> b?.getByte(k) }
        adapters[Char::class] = { b, k -> b?.getChar(k) }
        adapters[CharSequence::class] = { b, k -> b?.getCharSequence(k) }

        // 可序列化类型
        adapters[Parcelable::class] = { b, k -> b?.getParcelable(k) }
        adapters[Serializable::class] = { b, k -> b?.getSerializable(k) }

        // 数组类型
        adapters[IntArray::class] = { b, k -> b?.getIntArray(k) }
        adapters[FloatArray::class] = { b, k -> b?.getFloatArray(k) }
        adapters[LongArray::class] = { b, k -> b?.getLongArray(k) }
        adapters[DoubleArray::class] = { b, k -> b?.getDoubleArray(k) }
        adapters[BooleanArray::class] = { b, k -> b?.getBooleanArray(k) }
        adapters[ByteArray::class] = { b, k -> b?.getByteArray(k) }
        adapters[CharArray::class] = { b, k -> b?.getCharArray(k) }
        adapters[ShortArray::class] = { b, k -> b?.getShortArray(k) }
        adapters[Array<Parcelable>::class] = { b, k -> b?.getParcelableArray(k) }
    }

    /*fun <T: Any> register(type: KClass<T>, adapter: (Bundle?, String) -> T?) {
        adapters[type] = adapter
    }*/

    fun getAdapter(type: KClass<*>): ((Bundle?, String) -> Any?)? {
        val javaType = type.java

        adapters[type]?.let { return it }

        return when {
            javaType.isArray -> { b, k -> b?.get(k) }

            // todo 待验证
//            ArrayList::class.java.isAssignableFrom(javaType) -> adapter@{ b, k ->
//                val raw = b?.get(k) as? ArrayList<*>
//                if (raw.isNullOrEmpty()) return@adapter null // 空列表直接返回
//
//                when (val first = raw.first()) {
//                    is String -> b.getStringArrayList(k)
//                    is Int -> b.getIntegerArrayList(k)
//                    is Parcelable -> b.getParcelableArrayList<Parcelable>(k)
//                    else -> throw IllegalArgumentException("Unsupported ArrayList type for key=$k, first element type=${first::class.java}")
//                }
//            }

            Parcelable::class.java.isAssignableFrom(javaType) -> { b, k -> b?.getParcelable(k) }

            Serializable::class.java.isAssignableFrom(javaType) -> { b, k -> b?.getSerializable(k) }

            else -> null
        }
    }
}
