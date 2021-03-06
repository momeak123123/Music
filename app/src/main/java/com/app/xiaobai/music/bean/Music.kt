package com.app.xiaobai.music.bean

import java.io.Serializable


/**
 * 作者：yonglong on 2016/8/9 10:50
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
data class Music(
    val name: String,
    val album_name: String,
    val album_id: Long,
    val song_id: Long,
    val uri: String,
    val all_artist: List<artistlist>,
    var pic_url: String,
    val song_list_id: Long,
    val publish_time: String
) : Serializable

data class artistlist(
    val id: Long,
    val name: String
)

