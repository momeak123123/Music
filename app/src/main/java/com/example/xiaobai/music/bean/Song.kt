package com.example.xiaobai.music.bean


data class SongDet(
    val song: Music,
    var type: Int
)

data class SongList(
    val song_list_id: Long,
    var song_id: Long
)


data class SongLists(
    val play_list_id: Long,
    val name: String,
    val pic_url: String,
    val song_num: String,
    var create_time: String
)