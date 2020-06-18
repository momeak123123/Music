package com.example.music.music.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.music.bean.Music
import com.example.music.bean.ResultBeans
import com.example.music.common.Constants
import com.example.music.music.view.act.MusicPlayActivity
import com.example.music.sql.bean.Down
import com.example.music.sql.bean.Playlist
import com.example.music.sql.dao.mDownDao
import com.example.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
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
        fun addSong(context: Context, songid: MutableList<Music>, playid: Long) {

            val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
            val idmap = mutableListOf<Long>()
            val playlist: Playlist = mPlaylistDao.query(playid)[0]
            Observable.just(0)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                    }
                    override fun onNext(t: Int) {
                        if (mDownDao.query(playid).size > 0) {
                            for (sea in songid) {
                                for (det in mDownDao.query(playid)) {
                                    if (sea.song_id == det.song_id) {
                                        songid.remove(sea)
                                    }
                                }
                            }
                        }
                    }
                    override fun onComplete() {
                        if(songid.size>0){
                            val num = ((playlist.song_num).toInt() + songid.size).toString()
                            playlist.song_num = num
                            mPlaylistDao.update(playlist)
                            for (it in songid) {
                                val down = Down()
                                down.playid = playid
                                down.song_id = it.song_id
                                idmap.add(it.song_id)
                                down.name = it.name
                                down.album_name = it.album_name
                                down.album_id = it.album_id
                                down.uri = it.uri
                                var srtist_name = ""
                                for (its in it.all_artist) {
                                    if (srtist_name != "") {
                                        srtist_name += "/" + its.name
                                    } else {
                                        srtist_name = its.name
                                    }

                                }
                                down.all_artist = srtist_name
                                down.pic_url = it.pic_url
                                down.publish_time = it.publish_time
                                down.song_list_id = it.song_list_id
                                down.type = 0
                                mDownDao.insert(down)

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
                                                Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                                            Observable.just(num).subscribe(MusicPlayActivity.observer)
                                            Toast.makeText(
                                                context,
                                                bean.msg,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } catch (e: Exception) {
                                        }
                                    }
                                })
                        }else{
                            Toast.makeText(
                                context,
                               "歌曲已存在",
                                Toast.LENGTH_LONG
                            ).show()
                        }


                    }

                    override fun onError(e: Throwable) {

                    }
                })
        }
    }
}

