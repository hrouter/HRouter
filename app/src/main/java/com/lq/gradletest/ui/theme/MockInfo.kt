package com.lq.gradletest.ui.theme

import android.os.Parcel
import android.os.Parcelable

data class MockInfo(
    var mockName: String,
    var mockId: String,
    var mockAccount: Float
) : Parcelable {

    // 必须添加：从 Parcel 恢复的构造函数
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mockName)
        parcel.writeString(mockId)
        parcel.writeFloat(mockAccount)
    }

    override fun describeContents(): Int = 0

    companion object {
        // 关键：必须要有 CREATOR！
        @JvmField
        val CREATOR: Parcelable.Creator<MockInfo> = object : Parcelable.Creator<MockInfo> {
            override fun createFromParcel(parcel: Parcel): MockInfo {
                return MockInfo(parcel)
            }

            override fun newArray(size: Int): Array<MockInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}