package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.*
import com.example.music.common.Constants
import com.example.music.music.contract.FindContract
import com.example.music.music.view.act.LoginActivity
import com.example.music.music.view.fragment.FindFragment
import com.example.music.sql.bean.Playlist
import com.example.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class FindModel : BaseModel(), FindContract.IModel {
    override fun addSongList(context: Context, et_name: String) {


        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

        OkGo.post<String>(Constants.URL + "api/user/create_play_list")
            .params("token", sp.getString("token", ""))
            .params("name", et_name)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {

                            val song: Playlist = Gson().fromJson(
                                bean.data,
                                object : TypeToken<Playlist>() {}.type
                            )
                            mPlaylistDao.insert(song)
                            Observable.just(song).subscribe(FindFragment.observer)
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