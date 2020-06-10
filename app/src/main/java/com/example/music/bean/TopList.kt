package com.example.music.bean

data class TopList(
    val name : String,
    val pic_url : String,
    val info : String,
    val top_id: Long,
    val update_frequency: String,
    val type: Int,
    val from: Int,
    val from_id: Long
)