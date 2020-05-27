package com.example.music.bean

data class TopList(
    val cover : String,
    val info : String,
    val palylist_name : String,
    val playlist_id: Long,
    val update_frequency: String,
    val type: Int
)