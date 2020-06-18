package com.example.music.music.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.music.bean.Music
import com.example.music.bean.ResultBeans
import com.example.music.bean.SongList
import com.example.music.bean.TopList
import com.example.music.common.Constants
import com.example.music.music.view.act.MusicPlayActivity
import com.example.music.sql.bean.Down
import com.example.music.sql.bean.Playlist
import com.example.music.sql.dao.mDownDao
import com.example.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class MusicPlayModel {
    companion object {
        @SuppressLint("CheckResult")
        fun addSong(context: Context, song: MutableList<Music>,num:String, playid: Long) {

            val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

            val idmap = mutableListOf<Long>()
            val playlist: Playlist = mPlaylistDao.query(playid)[0]
            playlist.song_num = num
            mPlaylistDao.update(playlist)
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
                                            down.artist = it.all_artist[0].name
                                            down.artist_id = it.all_artist[0].id
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
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                        }
                    }
                })
        }
    }
}

