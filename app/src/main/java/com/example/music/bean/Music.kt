package com.example.music.bean

import android.os.Parcel
import android.os.Parcelable


/**
 * 作者：yonglong on 2016/8/9 10:50
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
data class Music(var type: String) {

    // 歌曲id
    var id: Long = 0
    // 音乐标题
    var title: String = ""
    // 歌手
    var artist: String = ""
    // 歌手id
    var artistId: String = ""
    // 专辑
    var album: String = ""
    // 专辑id
    var albumId: String = ""
    // 专辑内歌曲个数
    var trackNumber: Int = 0
    // 持续时间
    var duration: Long = 0
    // 收藏
    var isLove: Boolean = false
    // [本地|网络]
    var isOnline: Boolean = true
    // 音乐路径
    var uri: String = ""
    // [本地|网络] 音乐歌词地址
    var lyric: String = ""
    // [本地|网络]专辑封面路径
    var coverUri: String = ""
    // [网络]专辑封面
    var coverBig: String = ""
    // [网络]small封面
    var coverSmall: String = ""
    // 文件名
    var fileName: String = ""
    // 文件大小
    var fileSize: Long = 0
    // 发行日期
    var year: String = ""
    //更新日期
    var date: Long = 0
    //在线歌曲是否限制播放，false 可以播放
    var isCp: Boolean = false
    //在线歌曲是否付费歌曲，false 不能下载
    var isDl: Boolean = true
    //收藏id
    var collectId: String = ""
    //音乐品质，默认标准模式
    var quality: Int = 128000

    //音乐品质选择
    var hq: Boolean = false //192
    var sq: Boolean = false //320
    var high: Boolean = false //999
    


}

