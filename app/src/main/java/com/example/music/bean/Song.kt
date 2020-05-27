package com.example.music.bean

data class Song(
    val album : Map<String,String>,
    val artists : List<Artister>,
    val long : String,
    val song_id: Long,
    val song_name: String,
    val type: Int
)

data class Artister(
    val artist_id: Long,
    val artist_name: String,
    val artist_picurl: String
)