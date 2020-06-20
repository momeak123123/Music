package  com.example.music.xiaobai.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.xiaobai.bean.*
import com.example.music.xiaobai.common.Constants
import com.example.music.xiaobai.music.contract.FindContract
import com.example.music.xiaobai.music.view.fragment.FindFragment
import com.google.gson.Gson
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
class FindModel : BaseModel(), FindContract.IModel {
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

                            Observable.just(bean.data).subscribe(FindFragment.observer)
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
                            Observable.just(bean.data).subscribe(FindFragment.observers)
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