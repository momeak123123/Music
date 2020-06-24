package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.bean.ResultBean
import com.example.xiaobai.music.bean.SearchBean
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.SearchListContract
import com.example.xiaobai.music.music.view.act.LoginActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/24
 * @Description input description
 **/
class SearchListModel : BaseModel(), SearchListContract.IModel {
    override fun data(context: Context, search: String) {

        OkGo.get<String>("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=20")
            .params("w", search)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), SearchBean::class.javaObjectType)
                        if (bean.code == 200) {

                            val user: Map<String, String> = Gson().fromJson(
                                bean.data,
                                object : TypeToken<Map<String, String>>() {}.type
                            )

                            Observable.just(true).subscribe(LoginActivity.observer)
                        } else {
                            Toast.makeText(
                                context,
                                bean.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                    }
                }
            })
    }
}