package com.app.xiaobai.music.bean

data class TopList(
    val top_id: Long,
    val name : String,
    val pic_url : String,
    val info : String,
    val update_frequency: String,
    val update_time: Long,
    val type: Int,
    val from: Int,
    val from_id: Long
)