package com.example.xiaobai.music.bean

import java.io.Serializable

data class ApkModel(
    val name : String,
    val uri : String,
    val priority: Int
) : Serializable