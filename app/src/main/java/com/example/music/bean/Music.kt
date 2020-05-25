package com.example.music.bean

import android.os.Parcel
import android.os.Parcelable


/**
 * 作者：yonglong on 2016/8/9 10:50
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
data class Music(var type: String?) : Parcelable {

    // 歌曲id
    var id: Long = 0
    // 音乐标题
    var title: String? = ""
    // 歌手
    var artist: String? = ""
    // 歌手id
    var artistId: String? = ""
    // 专辑
    var album: String? = ""
    // 专辑id
    var albumId: String? = ""
    // 专辑内歌曲个数
    var trackNumber: Int = 0
    // 持续时间
    var duration: Long = 0
    // 收藏
    var isLove: Boolean = false
    // 音乐路径
    var uri: String? = ""
    // [本地|网络] 音乐歌词地址
    var lyric: String? = ""
    // [本地|网络]专辑封面路径
    var coverUri: String? = ""
    // [网络]big封面
    var coverBig: String? = ""
    // [网络]small封面
    var coverSmall: String? = ""
    // 文件名
    var fileName: String? = ""
    // 文件大小
    var fileSize: Long = 0
    // 发行日期
    var year: String? = ""
    //更新日期
    var date: Long = 0
    //在线歌曲是否限制播放，false 可以播放
    var isCp: Boolean = false
    //在线歌曲是否付费歌曲，false 不能下载
    var isDl: Boolean = true
    //收藏id
    var collectId: String? = ""
    //音乐品质，默认标准模式
    var quality: Int = 128000

    //音乐品质选择
    var hq: Boolean = false //192
    var sq: Boolean = false //320
    var high: Boolean = false //999

    constructor(parcel: Parcel) : this(parcel.readString()) {
        id = parcel.readLong()
        title = parcel.readString()
        artist = parcel.readString()
        artistId = parcel.readString()
        album = parcel.readString()
        albumId = parcel.readString()
        trackNumber = parcel.readInt()
        duration = parcel.readLong()
        isLove = parcel.readByte() != 0.toByte()
        uri = parcel.readString()
        lyric = parcel.readString()
        coverUri = parcel.readString()
        coverBig = parcel.readString()
        coverSmall = parcel.readString()
        fileName = parcel.readString()
        fileSize = parcel.readLong()
        year = parcel.readString()
        date = parcel.readLong()
        isCp = parcel.readByte() != 0.toByte()
        isDl = parcel.readByte() != 0.toByte()
        collectId = parcel.readString()
        quality = parcel.readInt()
        hq = parcel.readByte() != 0.toByte()
        sq = parcel.readByte() != 0.toByte()
        high = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(artistId)
        parcel.writeString(album)
        parcel.writeString(albumId)
        parcel.writeInt(trackNumber)
        parcel.writeLong(duration)
        parcel.writeByte(if (isLove) 1 else 0)
        parcel.writeString(uri)
        parcel.writeString(lyric)
        parcel.writeString(coverUri)
        parcel.writeString(coverBig)
        parcel.writeString(coverSmall)
        parcel.writeString(fileName)
        parcel.writeLong(fileSize)
        parcel.writeString(year)
        parcel.writeLong(date)
        parcel.writeByte(if (isCp) 1 else 0)
        parcel.writeByte(if (isDl) 1 else 0)
        parcel.writeString(collectId)
        parcel.writeInt(quality)
        parcel.writeByte(if (hq) 1 else 0)
        parcel.writeByte(if (sq) 1 else 0)
        parcel.writeByte(if (high) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Music> {
        override fun createFromParcel(parcel: Parcel): Music {
            return Music(parcel)
        }

        override fun newArray(size: Int): Array<Music?> {
            return arrayOfNulls(size)
        }
    }


}



