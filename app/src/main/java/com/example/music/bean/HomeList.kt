package com.example.music.bean

import android.os.Parcel
import android.os.Parcelable

data class HomeList(val imageUrl: String?, val title: String?, val txt: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(title)
        parcel.writeString(txt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeList> {
        override fun createFromParcel(parcel: Parcel): HomeList {
            return HomeList(parcel)
        }

        override fun newArray(size: Int): Array<HomeList?> {
            return arrayOfNulls(size)
        }
    }
}