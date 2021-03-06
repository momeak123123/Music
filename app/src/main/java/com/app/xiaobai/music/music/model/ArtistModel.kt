package  com.app.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.app.xiaobai.music.R
import com.app.xiaobai.music.bean.Hierarchy
import com.app.xiaobai.music.bean.ResultBean
import com.app.xiaobai.music.bean.ResultBeans
import com.app.xiaobai.music.config.Constants
import com.app.xiaobai.music.music.contract.ArtistContract
import com.app.xiaobai.music.music.view.act.ArtistActivity
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistModel : BaseModel(), ArtistContract.IModel {
    override fun taglist(context: Context, bool: Boolean) {


        OkGo.get<String>(Constants.URL + "artist/get_cat")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                        if (bean.code == 200) {
                            val hierarchy1 = Gson().fromJson<Array<Hierarchy>>(
                                bean.data.getAsJsonArray("hierarchy_1"),
                                Array<Hierarchy>::class.java
                            ).toMutableList()
                            val hierarchy2 = Gson().fromJson<Array<Hierarchy>>(
                                bean.data.getAsJsonArray("hierarchy_2"),
                                Array<Hierarchy>::class.java
                            ).toMutableList()
                            val h1 = Gson().toJson(hierarchy1)
                            val h2 = Gson().toJson(hierarchy2)

                            val sp: SharedPreferences =
                                context.getSharedPreferences("Music", Context.MODE_PRIVATE)

                            sp.edit().putString("h1", h1).apply()
                            sp.edit().putString("h2", h2).apply()

                            if (bool) {
                                Observable.just(bean.data).subscribe(ArtistActivity.observer)
                            }

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

    override fun listdata(context: Context, varieties: Int, letter: Int,page :Int) {


        OkGo.get<String>(Constants.URL + "artist/get_artist_list")
            .params("varieties", varieties)
            .params("letter", letter)
            .params("page", page)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                        if (bean.code == 200) {

                            Observable.just(bean.data).subscribe(ArtistActivity.observers)
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
                            R.string.error_connection,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })


    }
}