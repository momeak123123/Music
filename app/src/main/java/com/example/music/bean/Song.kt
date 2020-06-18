package com.example.music.bean

import android.os.Parcel
import android.os.Parcelable
import com.example.music.sql.bean.Down


data class SongDet(
    val song: Music,
    var type: Int
)

data class SongList(
    val song_list_id: Long,
    var song_id: Long
)