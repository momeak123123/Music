package com.example.music.xiaobai.bean


data class SongDet(
    val song: Music,
    var type: Int
)

data class SongList(
    val song_list_id: Long,
    var song_id: Long
)