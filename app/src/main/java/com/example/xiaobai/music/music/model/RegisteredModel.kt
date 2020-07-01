package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.ResultBean
import com.example.xiaobai.music.bean.ResultBeans
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.RegisteredContract
import com.example.xiaobai.music.music.view.act.RegisteredActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class RegisteredModel : BaseModel(), RegisteredContract.IModel {
    override fun registerdata(
        context: Context,
        email: String,
        pass: String
    ): Boolean {

        OkGo.post<String>(Constants.URL + "api/login/registered")
            .params("user_email", email)
            .params("password", pass)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBean::class.javaObjectType)

                        if (bean.code == 200) {
                            val user: Map<String, String> = Gson().fromJson(
                                bean.data,
                                object : TypeToken<Map<String, String>>() {}.type
                            )
                            val sp: SharedPreferences =
                                context.getSharedPreferences("User", Context.MODE_PRIVATE)
                            sp.edit().putString("username", user["username"]).apply()
                            sp.edit().putString("nickname", user["nickname"]).apply()
                            sp.edit().putString("url", user["headimgurl"]).apply()
                            sp.edit().putString("countries", user["countries"]).apply()
                            sp.edit().putBoolean("login", true).apply()
                            sp.edit().putString("sex", user["sex"]).apply()
                            sp.edit().putString("token", user["token"]).apply()
                            sp.edit().putString("message", user["message"]).apply()
                            sp.edit().putString("user_id", user["user_id"]).apply()
                            sp.edit().putString("follow", user["follow"]).apply()
                            sp.edit().putString("collect", user["collect"]).apply()
                            sp.edit().putString("like", user["like"]).apply()
                            Observable.just(true).subscribe(RegisteredActivity.observer)

                        }else{
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

        return false
    }

    override fun registercode(context: Context, email: String) {
        OkGo.post<String>(Constants.URL + "api/login/send_code")
            .params("user_email", email)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {
                            Toast.makeText(context, R.string.succes_code, Toast.LENGTH_SHORT).show()
                            Observable.just(true).subscribe(RegisteredActivity.observers)
                        } else {
                            Toast.makeText(
                                context,
                                bean.data.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {

                    }
                }
            })
    }
}