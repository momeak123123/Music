package com.example.music.xiaobai.bean

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class ResultBean (
    val code: Int,
    val msg: String,
    val data: JsonObject
)

data class ResultBeans (
    val code: Int,
    val msg: String,
    val data: JsonArray
)