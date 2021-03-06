package  com.app.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.app.xiaobai.music.R
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.bean.ResultBeans
import com.app.xiaobai.music.config.Constants
import com.app.xiaobai.music.music.contract.SongDetContract
import com.app.xiaobai.music.music.view.act.SongDetActivity
import com.app.xiaobai.music.sql.bean.Playlist
import com.app.xiaobai.music.sql.dao.mCollectDao
import com.app.xiaobai.music.sql.dao.mPlaylistDao
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
        OkGo.post<String>(Constants.URL + "user/get_song_list")
            .params("play_list_id", id)
            .params("token", sp.getString("token", ""))
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
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            R.string.error_connection,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })


    }

    override fun deldata(context: Context, ids: Long, playids: Long) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.post<String>(Constants.URL + "user/del_play_list")
            .params("play_list_id", playids)
            .params("token", sp.getString("token", ""))
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
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            R.string.error_connection,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    override fun delsong(context: Context, song: MutableList<Music>, playids: Long) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        val idmap = mutableListOf<Long>()
        for (it in song) {
            idmap.add(it.song_list_id)
        }
        OkGo.post<String>(Constants.URL + "user/del_song_list")
            .params("song_list_id", Gson().toJson(idmap))
            .params("token", sp.getString("token", ""))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {

                            val playlist: Playlist = mPlaylistDao.query(playids)[0]
                            val num = (playlist.song_num.toInt() - song.size).toString()
                            playlist.song_num = num
                            SongDetActivity.adapter.num = num
                            mPlaylistDao.update(playlist)

                            for (it in song) {
                                SongDetActivity.adapter.removedata(it)

                                val lists = mCollectDao.querys(it.song_id)
                                if (lists.size > 0) {
                                    mCollectDao.delete(lists[0].id)
                                }

                            }
                            SongDetActivity.adapter.notifyDataSetChanged()

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

    override fun delsongs(context: Context, song: Music, data: Int, playids: Long) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        val idmap = mutableListOf<Long>()
        idmap.add(song.song_list_id)
        OkGo.post<String>(Constants.URL + "user/del_song_list")
            .params("song_list_id", Gson().toJson(idmap))
            .params("token", sp.getString("token", ""))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {
                            val playlist: Playlist = mPlaylistDao.query(playids)[0]
                            val num = (playlist.song_num.toInt() - 1).toString()
                            playlist.song_num = num
                            mPlaylistDao.update(playlist)
                            SongDetActivity.adapter.num = num
                            SongDetActivity.adapter.notifyItemChanged(0)
                            SongDetActivity.adapter.remove(data)

                            val lists = mCollectDao.querys(song.song_id)
                            if (lists.size > 0) {
                                mCollectDao.delete(lists[0].id)


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

}