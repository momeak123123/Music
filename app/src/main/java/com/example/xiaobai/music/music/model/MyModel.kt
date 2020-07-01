package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.bean.ResultBean
import com.example.xiaobai.music.bean.ResultBeans
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.MyContract
import com.example.xiaobai.music.music.view.fragment.FindFragment
import com.example.xiaobai.music.music.view.fragment.MyFragment
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
class MyModel : BaseModel(), MyContract.IModel {
    override fun data(context: Context) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

        OkGo.post<String>(Constants.URL + "api/user/get_user_info")
            .params("token", sp.getString("token", ""))
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

                            Observable.just(true).subscribe(MyFragment.observer)
                        } else {
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
    }

    override fun addSongList(context: Context, et_name: String) {


        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

        OkGo.post<String>(Constants.URL + "api/user/create_play_list")
            .params("token", sp.getString("token", ""))
            .params("name", et_name)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                        if (bean.code == 200) {

                            Observable.just(bean.data).subscribe(MyFragment.observert)
                        } else {
                            Toast.makeText(
                                context,
                                bean.msg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "程序出现了未知异常",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })


    }

    override fun listdata(context: Context) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

        OkGo.post<String>(Constants.URL + "api/user/get_play_list")
            .params("token", sp.getString("token", ""))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {
                            Observable.just(bean.data).subscribe(MyFragment.observers)
                        } else {
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
    }

}