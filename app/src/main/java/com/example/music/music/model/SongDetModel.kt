package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.ResultBean
import com.example.music.bean.ResultBeans
import com.example.music.common.Constants
import com.example.music.music.contract.SongDetContract
import com.example.music.music.view.act.AlbumDetActivity
import com.example.music.music.view.act.SongDetActivity
import com.example.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class SongDetModel : BaseModel(), SongDetContract.IModel {
    override fun listdata(context: Context, id: Long) {
            val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
            OkGo.post<String>(Constants.URL + "api/user/get_song_list")
                .params("play_list_id",id)
                .params("token",sp.getString("token", ""))
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        /**
                         * 成功回调
                         */
                        try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {
                            Observable.just(bean.data).subscribe(SongDetActivity.observer)
                        } else {
                            Toast.makeText(
                                context,
                                bean.msg,
                                Toast.LENGTH_LONG
                            ).show()
                        }
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

    override fun deldata(context: Context, ids: Long,playids:Long) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.post<String>(Constants.URL + "api/user/del_play_list")
            .params("play_list_id",playids)
            .params("token",sp.getString("token", ""))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {
                            mPlaylistDao.delete(ids)
                            Observable.just(true).subscribe(SongDetActivity.observers)
                        } else {
                            Toast.makeText(
                                context,
                                bean.msg,
                                Toast.LENGTH_LONG
                            ).show()
                        }
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

    override fun delsongs(context: Context, data: Int, songids: Long) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.post<String>(Constants.URL + "api/user/del_song_list")
            .params("song_list_id",songids)
            .params("token",sp.getString("token", ""))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {
                            // mPlaylistDao.delete(ids)
                            SongDetActivity.adapter.remove(data)
                        } else {
                            Toast.makeText(
                                context,
                                bean.msg,
                                Toast.LENGTH_LONG
                            ).show()
                        }
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