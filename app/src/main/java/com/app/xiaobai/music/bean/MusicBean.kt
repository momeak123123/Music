package com.app.xiaobai.music.bean

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class qqmusic(

    val code: Int,
    val message: String,
    val notice: String,
    val tips: String,
    val subcode: Int,
    val time: Long,
    val data: JsonObject

)

data class kugoumusic(

    val error_code: Int,
    val error_msg: String,
    val status: Int,
    val data: JsonObject

)

data class kugousearch(

    val recordcount: Int,
    val error: String,
    val status: Int,
    val errcode: Int,
    val data: JsonArray

)

data class kugouseBean(

    val songcount: Int,
    val keyword: String,
    val searchcount: Int

)

data class baidumusic(

    val error_code: Int,
    val result: JsonObject

)

data class wangyimusic(

    val code: Int,
    val result: JsonObject

)

data class kuwomusic(

    val ARTISTPIC: String,
    val HIT: String,
    val HITMODE: String,
    val HIT_BUT_OFFLINE: String,
    val MSHOW: String,
    val NEW: String,
    val PN: String,
    val RN: String,
    val SHOW: String,
    val TOTAL: String,
    val searchgroup: String,
    val abslist: JsonArray

)

data class musicpath(

    val mid: String,
    val br: String,
    val geturl: String

)