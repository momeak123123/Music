package com.example.music.bean

import android.os.Parcel
import android.os.Parcelable

data class Song (
    val album : Map<String,String>,
    val artists : List<Artister>,
    val song_long : String?,
    val song_id: Long,
    val song_name: String?,
    val type: Int
)

data class Artister(
    val artist_id: Long,
    val artist_name: String,
    val artist_picurl: String
)

data class SongDet(
    val song: Song,
    var type: Int
)