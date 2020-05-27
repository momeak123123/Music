package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.ResultBean
import com.example.music.common.Constants
import com.example.music.music.contract.LoginContract
import com.google.gson.Gson
import com.lzy.okgo.OkGo
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
        OkGo.get<String>(Constants.URL + "api")
            .execute(object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                /**
                 * 成功回调
                 */
                val bean =
                    Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                if (bean.code == 200) {


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
