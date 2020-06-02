package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.*
import com.example.music.common.Constants
import com.example.music.music.contract.FindContract
import com.example.music.music.view.act.LoginActivity
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
    override fun addSongList(context:Context,et_name: String) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

        OkGo.get<String>(Constants.URL + "api/login/login")
            .params("token",sp.getString("token", ""))
            .params("playlist_name",et_name)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    val bean =
                        Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                    if (bean.code == 200) {

                        val user: Map<String,String> = Gson().fromJson(
                            bean.data,
                            object : TypeToken<Map<String, String>>() {}.type
                        )

                        Observable.just(true).subscribe(LoginActivity.observer)
                    } else {
                        Toast.makeText(
                            context,
                            bean.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }
}