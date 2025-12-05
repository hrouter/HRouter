package com.lq.lib_api.autowired

import android.os.Bundle
import android.os.Parcelable
import com.lq.lib_api.exception.PutTypeIllegalException
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

    fun parcelable(key:String,value: Parcelable){
        bundle.putParcelable(key,value)
    }

    fun serializable(key:String ,value: Serializable){
        bundle.putSerializable(key,value)
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
            is Parcelable ->parcelable(key,value)
            is Serializable ->serializable(key,value)
            else-> throw PutTypeIllegalException("$value")
        }
    }


}