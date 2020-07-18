package com.example.xiaobai.music.music.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.StartsActivity
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.config.Constants
import com.example.xiaobai.music.config.OSS
import com.example.xiaobai.music.music.view.act.AlbumDetActivity
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.example.xiaobai.music.music.view.act.UserEditActivity
import com.example.xiaobai.music.music.view.fragment.HomeFragment
import com.example.xiaobai.music.sql.bean.Collect
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mCollectDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.example.xiaobai.music.utils.BitmapUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import constant.UiType
import io.reactivex.Observable
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import update.UpdateAppUtils
import java.util.*

class MusicPlayModel {
    companion object {

        private var apkUrl = ""
        private var updateTitle = ""
        private var updateContent = ""


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
            OkGo.post<String>(Constants.URL + "user/add_play_list")
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
                                Toast.makeText(
                                    context,
                                    context.getText(R.string.song_cancel_success),
                                    Toast.LENGTH_SHORT
                                ).show()

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
                                            val collect = Collect()
                                            collect.playid = playid
                                            collect.song_id = it.song_id
                                            collect.name = it.name
                                            collect.album_name = it.album_name
                                            collect.album_id = it.album_id
                                            collect.uri = it.uri
                                            collect.artist = it.all_artist[0].name
                                            collect.artist_id = it.all_artist[0].id
                                            collect.pic_url = it.pic_url
                                            collect.publish_time = it.publish_time
                                            collect.song_list_id = it.song_list_id
                                            mCollectDao.insert(collect)
                                        }
                                    }
                                }

                            } else {
                                Toast.makeText(
                                    context,
                                    bean.msg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {
                        }
                    }
                })
        }


        fun updateapp(version: String) {

            OkGo.get<String>(Constants.URL + "Version/index")
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
                                val context = bean.update_log.split("|")
                                for (it in context) {
                                    if (updateContent == "") {
                                        updateContent = it + "\n"
                                    } else {
                                        updateContent += it + "\n"
                                    }

                                }
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

        fun getadvertising() {
            OkGo.get<String>(Constants.URL + "ads/get_ads")
                .params("type", 1)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        /**
                         * 成功回调
                         */
                        try {
                            val bean =
                                Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                            if (bean.code == 200) {

                                val ads: List<Ads> = Gson().fromJson<Array<Ads>>(
                                    bean.data,
                                    Array<Ads>::class.java
                                ).toList()
                                MusicApp.setAdstime(ads[0].seconds)
                                object : Thread() {
                                    override fun run() {
                                        if (ads[0].img != "") {
                                            val bm = BitmapUtils.getBitmap(ads[0].img)
                                            MusicApp.setStartback(bm)
                                        }

                                    }
                                }.start()

                            }
                        } catch (e: Exception) {

                        }
                    }

                })

        }


        fun downnum(context: Context) {
            val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
            OkGo.post<String>(Constants.URL + "user/reduce_times")
                .params("token", sp.getString("token", ""))
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        /**
                         * 成功回调
                         */
                        try {
                            val bean =
                                Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                            if (bean.code == 200) {
                               val num = bean.data.getAsJsonObject("num").asInt
                                MusicApp.setMinute(num)
                            }

                        } catch (e: Exception) {
                        }

                    }
                })
        }

    }


}

