package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.ResultBean
import com.example.music.bean.ResultBeans
import com.example.music.common.Constants
import com.example.music.music.contract.UserEditContract
import com.example.music.music.view.act.RegisteredActivity
import com.example.music.music.view.act.UserEditActivity
import com.example.music.music.view.fragment.MyFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
class UserEditModel : BaseModel(), UserEditContract.IModel{

    override fun registerdata(context: Context, name: String, gender: Int, city: String, images: String) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.post<String>(Constants.URL + "api/user/up_user_info")
            .params("token",sp.getString("token", ""))
            .params("nickname",name)
            .params("sex",gender)
            .params("countries",city)
            .params("images",images)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    val bean =
                        Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                    if (bean.code == 200) {

                        val user: Map<String, String> = Gson().fromJson(
                            bean.data,
                            object : TypeToken<Map<String, String>>() {}.type
                        )

                        sp.edit().putString("username", user["username"]).apply()
                        sp.edit().putString("nickname", user["nickname"]).apply()
                        sp.edit().putString("url", user["headimgurl"]).apply()
                        sp.edit().putString("countries", user["countries"]).apply()
                        sp.edit().putString("sex", user["sex"]).apply()
                        sp.edit().putString("token", user["token"]).apply()
                        sp.edit().putString("user_id", user["user_id"]).apply()
                        sp.edit().putString("follow", user["follow"]).apply()
                        sp.edit().putString("collect", user["collect"]).apply()
                        sp.edit().putString("like", user["like"]).apply()

                        Observable.just(true).subscribe(UserEditActivity.observer)
                    }
                        Toast.makeText(
                            context,
                            bean.msg,
                            Toast.LENGTH_LONG
                        ).show()


                }
            })
    }

}