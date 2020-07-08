package com.example.xiaobai.music.bean

import com.google.gson.JsonObject

data class Sear (
    val serach_id: Int,
    val name: String
)

data class Searchs (
    val type: Int,
    val music: Music
)