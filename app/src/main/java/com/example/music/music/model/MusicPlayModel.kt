package com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.ResultBeans
import com.example.music.common.Constants
import com.example.music.music.view.fragment.FindFragment
import com.example.music.sql.bean.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable

class MusicPlayModel {
    companion object {
        fun addSong(context: Context, songid: Long, playid: Long) {

            val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
            println(sp.getString("token", ""))
            OkGo.post<String>(Constants.URL + "api/user/add_play_list")
                .params("token", sp.getString("token", ""))
                .params("play_list_id", playid)
                .params("song_id", songid)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        /**
                         * 成功回调
                         */
                        try {
                            val bean =
                                Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                                Toast.makeText(
                                    context,
                                    bean.msg,
                                    Toast.LENGTH_LONG
                                ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "程序出现了未知异常",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })


        }
    }
}