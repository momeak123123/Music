package com.example.music.bean

import android.os.Parcel
import android.os.Parcelable


/**
 * 作者：yonglong on 2016/8/9 10:50
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
data class Music(
    var id: Long = 0,
    var song_id: Long = 0,
    var title: String = "",
    var author: String = "",
    var lrclink: String = "",
    var type: Int = 0,
    var uri: String = ""
)



