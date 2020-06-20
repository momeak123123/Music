package com.example.music.xiaobai.music.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.xiaobai.bean.Music
import com.example.music.xiaobai.bean.ResultBeans
import com.example.music.xiaobai.bean.SongList
import com.example.music.xiaobai.common.Constants
import com.example.music.xiaobai.music.view.act.AlbumDetActivity
import com.example.music.xiaobai.music.view.act.MusicPlayActivity
import com.example.music.xiaobai.sql.bean.Down
import com.example.music.xiaobai.sql.bean.Playlist
import com.example.music.xiaobai.sql.dao.mDownDao
import com.example.music.xiaobai.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response

class MusicPlayModel {
    companion object {
        @SuppressLint("CheckResult")
        fun addSong(context: Context, song: MutableList<Music>,num:String, playid: Long,type :Int,position:Int) {

            val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

            val idmap = mutableListOf<Long>()

            for(it in song){
                idmap.add(it.song_id)
            }
            OkGo.post<String>(Constants.URL + "api/user/add_play_list")
                .params("token", sp.getString("token", ""))
                .params("play_list_id", playid)
                .params("song_id", Gson().toJson(idmap))
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        /**
                         * 成功回调
                         */
                        try {
                            val bean =
                                Gson().fromJson(
                                    response.body(),
                                    ResultBeans::class.javaObjectType
                                )
                            if(bean.code==200){

                                val playlist: Playlist = mPlaylistDao.query(playid)[0]
                                playlist.song_num = num
                                mPlaylistDao.update(playlist)

                                if(type==0){
                                    AlbumDetActivity.adapters.update(position, num)
                                }else{
                                    MusicPlayActivity.adapter.update(position, num)
                                }

                                val list: List<SongList> = Gson().fromJson(
                                    bean.data,
                                    object : TypeToken<List<SongList>>() {}.type
                                )
                                for(i in list){
                                    for (it in song) {
                                        if (i.song_id == it.song_id) {
                                            val down = Down()
                                            down.playid = playid
                                            down.song_id = it.song_id
                                            idmap.add(it.song_id)
                                            down.name = it.name
                                            down.album_name = it.album_name
                                            down.album_id = it.album_id
                                            down.uri = it.uri
                                            down.artist = it.all_artist[0].artist_name
                                            down.artist_id = it.all_artist[0].artist_id
                                            down.pic_url = it.pic_url
                                            down.publish_time = it.publish_time
                                            down.song_list_id = i.song_list_id
                                            down.type = 0
                                            mDownDao.insert(down)
                                        }
                                    }
                                }

                            }
                            Toast.makeText(
                                context,
                                bean.msg,
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                        }
                    }
                })
        }
    }
}

