package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.ResultBeans
import com.example.xiaobai.music.bean.ResultBeant
import com.example.xiaobai.music.bean.SongList
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.ChangePassContract
import com.example.xiaobai.music.music.view.act.AlbumDetActivity
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.example.xiaobai.music.music.view.fragment.HomeFragment
import com.example.xiaobai.music.sql.bean.Collect
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mCollectDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
class ChangePassModel : BaseModel(), ChangePassContract.IModel {
    override fun data(context: Context, pass: String, passs: String) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.post<String>(Constants.URL + "api/user/edit_user_passwd")
            .params("token", sp.getString("token", ""))
            .params("or_pw", pass)
            .params("now_pw", passs)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */

                    val bean =
                        Gson().fromJson(
                            response.body(),
                            ResultBeant::class.javaObjectType
                        )
                    Toast.makeText(
                        context,
                        bean.msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}