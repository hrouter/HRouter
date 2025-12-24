package com.lq.lib_api.autowired

import android.os.Bundle
import android.os.Parcelable
import com.lq.lib_api.exception.PutTypeIllegalException
import com.lq.lib_api.util.LogUtil
import java.io.Serializable

class ParameterBuilder {
    val bundle = Bundle()

    fun string(key:String,value:String){
        bundle.putString(key,value)
    }

    fun int(key:String,value:Int){
        bundle.putInt(key,value)
    }

    fun float(key:String,value:Float){
        bundle.putFloat(key,value)
    }

    fun double(key: String, value:Double){
        bundle.putDouble(key,value)
    }

    fun long(key:String,value:Long){
        bundle.putLong(key,value)
    }

    fun boolean(key:String,value: Boolean){
        bundle.putBoolean(key,value)
    }

    fun short(key:String,value:Short){
        bundle.putShort(key,value)
    }

    fun parcelable(key:String,value: Parcelable){
        bundle.putParcelable(key,value)
    }

    fun serializable(key:String ,value: Serializable){
        bundle.putSerializable(key,value)
    }

    fun floatArray(key:String,value: FloatArray){
        bundle.putFloatArray(key,value)
    }

    fun intArray(key:String,value: IntArray){
        bundle.putIntArray(key,value)
    }

    fun longArray(key:String,value: LongArray){
        bundle.putLongArray(key,value)
    }

    fun doubleArray(key:String,value: DoubleArray){
        bundle.putDoubleArray(key,value)
    }

    fun shortArray(key:String,value: ShortArray){
        bundle.putShortArray(key,value)
    }

    fun booleanArray(key:String,value: BooleanArray){
        bundle.putBooleanArray(key,value)
    }

    fun charArray(key:String,value: CharArray){
        bundle.putCharArray(key,value)
    }

    infix fun String.to(value:Any?){
        parameter(this,value)
    }

    fun <T> parameter(key: String,value:T){
        when(value){
            is String -> string(key,value)
            is Int-> int(key,value)
            is Double -> double(key,value)
            is Float -> float(key,value)
            is Long->long(key,value)
            is Boolean ->boolean(key,value)
            is Short -> short(key,value)

            is FloatArray -> floatArray(key,value)
            is IntArray -> intArray(key,value)
            is LongArray -> longArray(key,value)
            is DoubleArray -> doubleArray(key,value)
            is ShortArray -> shortArray(key,value)
            is BooleanArray ->booleanArray(key,value)
            is CharArray -> charArray(key,value)

            is Parcelable ->parcelable(key,value)
            is Serializable ->serializable(key,value)
            else-> throw PutTypeIllegalException("$value")
        }
    }


}