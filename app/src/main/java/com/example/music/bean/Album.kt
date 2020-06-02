package com.example.music.bean

data class Album (
    val album_id : Long,
    val album_name: String,
    val album_picurl: String,
    val astist_id : Long,
    val artist_name : String,
    val publish_time: String,
    val type: Int
)


data class AlbumDet (
    val album_id : Long,
    val album_name: String,
    val album_picurl: String,
    val publish_time: String
)