package  com.example.music.music.model

import android.content.Context
import android.widget.Toast
import com.example.music.bean.ResultBean
import com.example.music.bean.ResultBeans
import com.example.music.common.Constants
import com.example.music.music.contract.AlbumDetContract
import com.example.music.music.view.act.AlbumDetActivity
import com.example.music.music.view.act.ArtistActivity
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumDetModel : BaseModel(), AlbumDetContract.IModel {

    override fun songdata(id: Long, type: Int, context: Context) {

        OkGo.get<String>(Constants.URL + "api/album/getAlbumSonglist")
            .params("album_id", id)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                        if (bean.code == 200) {
                            Observable.just(bean.data).subscribe(AlbumDetActivity.observer)
                        } else {
                            Toast.makeText(
                                context,
                                bean.msg,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {

                    }

                }
            })


    }

    override fun songdatas(id: Long, type: Int, time: Long ,context: Context) {


        OkGo.get<String>(Constants.URL + "api/ranking/rankList")
            .params("from_id", id)
            .params("from", type)
            .params("update_time", time)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                        if (bean.code == 200) {
                            Observable.just(bean.data).subscribe(AlbumDetActivity.observer)
                        } else {
                            Toast.makeText(
                                context,
                                bean.data.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {

                    }

                }
            })


    }
}

