package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.ResultBean
import com.example.music.common.Constants
import com.example.music.music.contract.RegisteredContract
import com.example.music.music.view.act.LoginActivity
import com.example.music.music.view.act.RegisteredActivity
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
        pass: String,
        code: String
    ): Boolean {


        OkGo.get<String>(Constants.URL + "api/login/registered")
            .params("user_email", email)
            .params("password", pass)
            .params("email_code", code)
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
                                context.getSharedPreferences("Music", Context.MODE_PRIVATE)
                            sp.edit().putString("username", user["username"]).apply()
                            sp.edit().putString("nickname", user["nickname"]).apply()
                            sp.edit().putString("url", user["headimgurl"]).apply()
                            sp.edit().putString("countries", user["countries"]).apply()
                            sp.edit().putString("province", user["province"]).apply()
                            sp.edit().putString("city", user["city"]).apply()
                            sp.edit().putString("sex", user["sex"]).apply()
                            sp.edit().putString("token", user["token"]).apply()
                            sp.edit().putString("user_id", user["user_id"]).apply()
                            sp.edit().putString("follow", user["follow"]).apply()
                            sp.edit().putString("collect", user["collect"]).apply()
                            sp.edit().putString("like", user["like"]).apply()
                            Observable.just(true).subscribe(RegisteredActivity.observer)
                        } else {
                            Toast.makeText(
                                context,
                                bean.data.toString(),
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



        return false
    }

    override fun registercode(context: Context, email: String) {
        Observable.just(true).subscribe(RegisteredActivity.observers)
        /* OkGo.get<String>(Constants.URL + "api/login/registered")
             .params("user_email",email)
             .execute(object : StringCallback() {
                 override fun onSuccess(response: Response<String>) {
                     /**
                      * 成功回调
                      */
                     val bean =
                         Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                     if (bean.code == 200) {
                         Observable.just(true).subscribe(RegisteredActivity.observers)
                     } else {
                         Toast.makeText(
                             context,
                             bean.data.toString(),
                             Toast.LENGTH_SHORT
                         ).show()
                     }
                 }
             })*/
    }
}