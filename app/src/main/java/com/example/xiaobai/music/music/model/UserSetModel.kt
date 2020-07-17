package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.bean.ResultBeant
import com.example.xiaobai.music.config.Constants
import com.example.xiaobai.music.music.contract.UserSetContract
import com.example.xiaobai.music.music.view.act.UserSetActivity
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/07
 * @Description input description
 **/
class UserSetModel : BaseModel(), UserSetContract.IModel {
    override fun code(context :Context , code: String) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.post<String>(Constants.URL + "api/user/is_invite_code")
            .params("token", sp.getString("token", ""))
            .params("invite_code", code)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    if (response.code() == 200) {
                            val bean =
                                Gson().fromJson(response.body(), ResultBeant::class.javaObjectType)
                            if (bean.code == 200) {
                                Observable.just(code).subscribe(UserSetActivity.observer)

                            } else {
                                Toast.makeText(
                                    context,
                                    bean.msg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            })
    }

    override fun pass(context: Context, pass: String) {
        TODO("Not yet implemented")
    }
}