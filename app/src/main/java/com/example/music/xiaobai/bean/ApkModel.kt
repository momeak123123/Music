package com.example.music.xiaobai.bean

import java.io.Serializable

data class ApkModel(
    val name : String,
    val uri : String,
    val priority: Int
) : Serializable