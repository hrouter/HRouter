package com.lq.gradletest

import android.os.Parcel
import android.os.Parcelable

data class UserInfo(
    var userName: String,
    var id: String,
    var account: Float
) : Parcelable {

    // 必须添加：从 Parcel 恢复的构造函数
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(id)
        parcel.writeFloat(account)
    }

    override fun describeContents(): Int = 0

    companion object {
        // 关键：必须要有 CREATOR！
        @JvmField
        val CREATOR: Parcelable.Creator<UserInfo> = object : Parcelable.Creator<UserInfo> {
            override fun createFromParcel(parcel: Parcel): UserInfo {
                return UserInfo(parcel)
            }

            override fun newArray(size: Int): Array<UserInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}