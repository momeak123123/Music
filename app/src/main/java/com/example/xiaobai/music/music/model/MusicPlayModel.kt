package com.example.xiaobai.music.music.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.view.act.AlbumDetActivity
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.example.xiaobai.music.music.view.fragment.HomeFragment
import com.example.xiaobai.music.sql.bean.Down
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import constant.UiType
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import update.UpdateAppUtils

class MusicPlayModel {
    companion object {

        private var apkUrl = ""
        private var updateTitle = ""
        private var updateContent = "1、各种音乐随便听\n2、优化app性能\n3、增加搜索功能\n4、更多功能等你探索"


        @SuppressLint("CheckResult")
        fun addSong(
            context: Context,
            song: MutableList<Music>,
            num: String,
            playid: Long,
            type: Int,
            position: Int
        ) {

            val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

            val idmap = mutableListOf<Long>()

            for (it in song) {
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
                            if (bean.code == 200) {

                                val playlist: Playlist = mPlaylistDao.query(playid)[0]
                                playlist.song_num = num
                                mPlaylistDao.update(playlist)

                                when (type) {
                                    1 -> {
                                        AlbumDetActivity.adapters.update(position, num)
                                    }
                                    2 -> {
                                        HomeFragment.adaptert.update(position, num)
                                    }
                                    else -> {
                                        MusicPlayActivity.adapter.update(position, num)
                                    }
                                }

                                val list: List<SongList> = Gson().fromJson(
                                    bean.data,
                                    object : TypeToken<List<SongList>>() {}.type
                                )
                                for (i in list) {
                                    for (it in song) {
                                        if (i.song_id == it.song_id) {
                                            val down = Down()
                                            down.playid = playid
                                            down.song_id = it.song_id
                                            down.name = it.name
                                            down.album_name = it.album_name
                                            down.album_id = it.album_id
                                            down.uri = it.uri
                                            down.artist = it.all_artist[0].name
                                            down.artist_id = it.all_artist[0].id
                                            down.pic_url = it.pic_url
                                            down.publish_time = it.publish_time
                                            down.song_list_id = it.song_list_id
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


        fun updateapp(version: String) {

            OkGo.get<String>(Constants.URL + "api/Version/index")
                //.params("token", sp.getString("token", ""))
                .params("version", version)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        /**
                         * 成功回调
                         */
                        try {
                            val bean =
                                Gson().fromJson(
                                    response.body(),
                                    UpdateApp::class.javaObjectType
                                )

                            val update = bean.update
                            if (update == "YES") {
                                apkUrl = bean.apk_file_url
                                val versions = bean.new_version
                                updateTitle = "发现新版本V$versions"
                                updateContent = bean.update_log
                                val force = bean.constraint
                                UpdateAppUtils
                                    .getInstance()
                                    .apkUrl(apkUrl)
                                    .updateTitle(updateTitle)
                                    .updateContent(updateContent)
                                    .updateConfig(
                                        UpdateConfig(
                                            alwaysShowDownLoadDialog = true,
                                            force = force,
                                            notifyImgRes = R.mipmap.ic_launcher,
                                            serverVersionName = versions
                                        )
                                    )
                                    .uiConfig(
                                        UiConfig(
                                            uiType = UiType.CUSTOM,
                                            customLayoutId = R.layout.view_update_dialog_custom
                                        )
                                    )
                                    .setOnInitUiListener(object : OnInitUiListener {
                                        @SuppressLint("SetTextI18n")
                                        override fun onInitUpdateUi(
                                            view: View?,
                                            updateConfig: UpdateConfig,
                                            uiConfig: UiConfig
                                        ) {
                                            view?.findViewById<TextView>(R.id.tv_update_title)?.text =
                                                "版本更新啦"
                                            view?.findViewById<TextView>(R.id.tv_version_name)?.text =
                                                "V$versions"
                                            view?.findViewById<TextView>(R.id.tv_update_content)?.text =
                                                updateContent
                                        }
                                    })
                                    .update()
                            }

                        } catch (e: Exception) {
                        }
                    }
                })
        }

    }


}

