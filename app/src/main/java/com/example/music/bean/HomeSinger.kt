package com.example.music.bean

import android.os.Parcel
import android.os.Parcelable

data class HomeSinger(val imageUrl :String? ,val title:String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeSinger> {
        override fun createFromParcel(parcel: Parcel): HomeSinger {
            return HomeSinger(parcel)
        }

        override fun newArray(size: Int): Array<HomeSinger?> {
            return arrayOfNulls(size)
        }
    }

}