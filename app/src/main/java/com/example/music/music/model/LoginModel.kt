package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.ResultBean
import com.example.music.config.Okgo
import com.example.music.music.contract.LoginContract
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LoginModel : BaseModel(), LoginContract.IModel {
    override fun logindata(context: Context): Boolean {
        Okgo.getokgo("test",object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                /**
                 * 成功回调
                 */
                val bean =
                    Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                if (bean.code.toString() == "200") {
                    val id: String = bean.data.get("uniqueId").asString
                    val name: String = bean.data.get("nickname").asString
                    /*val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
                    sp.edit().putString("userid", id).apply()
                    sp.edit().putString("username", name).apply()
                    sp.edit().putString("login", "true").apply()*/
                } else {
                    Toast.makeText(
                        context,
                        bean.data.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
       return false
    }
}
