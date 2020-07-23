package  com.app.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.app.xiaobai.music.R
import com.app.xiaobai.music.bean.ResultBeans
import com.app.xiaobai.music.config.Constants
import com.app.xiaobai.music.music.contract.CodeListContract
import com.app.xiaobai.music.music.view.act.CodeListActivity
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
class CodeListModel : BaseModel(), CodeListContract.IModel {
    override fun codelist(context: Context) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.post<String>(Constants.URL + "user/get_invite_list")
            .params("token", sp.getString("token", ""))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    if(response.code()==200){
                        try {
                            val bean =
                                Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                            if (bean.code == 200) {
                                Observable.just(bean.data).subscribe(CodeListActivity.observer)
                            }
                        } catch (e: Exception) {

                        }
                    }else {
                        Toast.makeText(
                            context,
                            R.string.error_connection,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            })
    }
}