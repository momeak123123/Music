package com.example.music.bean

import com.google.gson.JsonObject

data class ResultBean (
    val code: Int,
    val msg: String,
    val data: JsonObject
)