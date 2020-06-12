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
class UserEditModel : BaseModel(), UserEditContract.IModel{

    override fun registerdata(context: Context, name: String, gender: String, city: String) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.get<String>(Constants.URL + "api/user/up_userinfo")
            .params("token",sp.getString("token", ""))
            .params("nickname",name)
            .params("sex",gender)
            .params("city",city)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    val bean =
                        Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                    if (bean.code == 200) {

                    } else {
                        Toast.makeText(
                            context,
                            bean.msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

}