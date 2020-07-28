package com.app.xiaobai.music.bean

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

data class ResultBeand (
    val code: Int,
    val msg: String,
    val total: Int,
    val data: JsonArray
)

data class ResultBeant (
    val code: Int,
    val msg: String

)

data class UpdateApp(
    val update: String,
    val new_version: String,
    val apk_file_url: String,
    val target_size: String,
    val constraint: Boolean,
    val update_log: String
)

data class SearchBean(
    val code: Int,
    val message: String,
    val notice: String,
    val tips: String,
    val subcode: Int,
    val data: JsonObject
)