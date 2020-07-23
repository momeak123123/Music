package com.app.xiaobai.music.bean

data class Album (
    val album_id : Long,
    val name: String,
    val pic_url: String,
    val artist_id : Long,
    val artist_name: String,
    val company : String,
    val publish_time: String,
    val type: Int,
    val info: String
)


data class AlbumDet (
    val album_id : Long,
    val album_name: String,
    val pic_url: String,
    val type : Int,
    val artist_id : Long,
    val artist_name: String,
    val publish_time: String,
    val song_list: List<Music>
)