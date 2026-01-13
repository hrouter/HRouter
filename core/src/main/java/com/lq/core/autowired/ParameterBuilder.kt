package com.lq.core.autowired

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

@Suppress("TooManyFunctions")
class ParameterBuilder {
    val bundle = Bundle()

    infix fun String.to(value: Int) = bundle.putInt(this, value)

    infix fun String.to(value: String?) = bundle.putString(this, value)

    infix fun String.to(value: Boolean) = bundle.putBoolean(this, value)

    infix fun String.to(value: Float) = bundle.putFloat(this, value)

    infix fun String.to(value: Double) = bundle.putDouble(this, value)

    infix fun String.to(value: Long) = bundle.putLong(this, value)

    infix fun String.to(value: Byte) = bundle.putByte(this, value)

    infix fun String.to(value: Char) = bundle.putChar(this, value)

    infix fun String.to(value: Short) = bundle.putShort(this, value)

    infix fun String.to(value: CharSequence?) = bundle.putCharSequence(this, value)

    infix fun String.to(value: Parcelable?) = bundle.putParcelable(this, value)

    infix fun String.to(value: Serializable?) = bundle.putSerializable(this, value)

    @JvmName("toParcelableSerializable")
    infix fun <T> String.to(value: T?) where T : Parcelable, T : Serializable = bundle.putParcelable(this, value)

    infix fun String.to(value: IntArray?) = bundle.putIntArray(this, value)

    infix fun String.to(value: FloatArray?) = bundle.putFloatArray(this, value)

    infix fun String.to(value: LongArray?) = bundle.putLongArray(this, value)

    infix fun String.to(value: DoubleArray?) = bundle.putDoubleArray(this, value)

    infix fun String.to(value: BooleanArray?) = bundle.putBooleanArray(this, value)

    infix fun String.to(value: ByteArray?) = bundle.putByteArray(this, value)

    infix fun String.to(value: CharArray?) = bundle.putCharArray(this, value)

    infix fun String.to(value: ShortArray?) = bundle.putShortArray(this, value)

    infix fun String.to(value: Array<out Parcelable>?) = bundle.putParcelableArray(this, value)

//    infix fun String.stringArrayList(value: ArrayList<String>?) = bundle.putStringArrayList(this, value)

//    infix fun String.intArrayList(value: ArrayList<Int>?) = bundle.putIntegerArrayList(this, value)

//    infix fun String.parcelableArrayList(value: ArrayList<out Parcelable>?) = bundle.putParcelableArrayList(this, value)

    @Deprecated("不支持此类型映射", level = DeprecationLevel.ERROR)
    infix fun <A, B> A.to(that: B): Pair<A, B> = Pair(this, that)
}
