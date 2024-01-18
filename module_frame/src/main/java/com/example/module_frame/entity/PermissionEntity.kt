package com.example.module_frame.entity

import android.os.Parcel
import android.os.Parcelable

data class PermissionEntity(
    val title: String,
    val msg: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(msg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PermissionEntity> {
        override fun createFromParcel(parcel: Parcel): PermissionEntity {
            return PermissionEntity(parcel)
        }

        override fun newArray(size: Int): Array<PermissionEntity?> {
            return arrayOfNulls(size)
        }
    }
}