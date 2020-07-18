package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.bean.ResultBeans
import com.example.xiaobai.music.config.Constants
import com.example.xiaobai.music.music.contract.SongEditContract
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/16
 * @Description input description
 **/
class SongEditModel : BaseModel(), SongEditContract.IModel {
    override fun registerdata(context: Context, name: String, playid: Long) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.get<String>(Constants.URL + "user/edit_play_list")
            .params("token",sp.getString("token", ""))
            .params("name",name)
            .params("play_list_id",playid)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    val bean =
                        Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        Toast.makeText(
                            context,
                            bean.msg,
                            Toast.LENGTH_SHORT
                        ).show()
                }
            })
    }
}