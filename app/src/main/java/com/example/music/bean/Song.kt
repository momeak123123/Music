package com.example.music.bean

import android.os.Parcel
import android.os.Parcelable


data class SongDet(
    val song: Music,
    var type: Int
)


data class SongList(
    val palylist_name: String,
    var playlist_id: String,
    var playlist_num: Int,
    var cover: String
)