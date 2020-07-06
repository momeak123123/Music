package  com.example.xiaobai.music.music.model

import android.content.Context
import android.widget.Toast
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.ResultBeans
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.AlbumDetContract
import com.example.xiaobai.music.music.view.act.AlbumDetActivity
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
class AlbumDetModel : BaseModel(), AlbumDetContract.IModel {

    override fun songdata(id: Long, type: Int, context: Context) {

        OkGo.get<String>(Constants.URL + "api/album/getAlbumSonglist")
            .params("album_id", id)
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
                                Observable.just(bean.data).subscribe(AlbumDetActivity.observer)
                            } else {
                                Toast.makeText(
                                    context,
                                    bean.msg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {

                        }
                    }else{
                        Toast.makeText(
                            context,
                            R.string.error_connection,
                            Toast.LENGTH_SHORT
                        ).show()
                        Observable.just(true).subscribe(AlbumDetActivity.observers)
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
                    if (response.code() == 200) {
                        /**
                         * 成功回调
                         */
                        try {
                            val bean =
                                Gson().fromJson(response.body(), ResultBeans::class.javaObjectType)
                            if (bean.code == 200) {
                                Observable.just(bean.data).subscribe(AlbumDetActivity.observer)
                            } else {
                                Toast.makeText(
                                    context,
                                    bean.msg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {

                        }

                    } else {
                        Toast.makeText(
                            context,
                            R.string.error_connection,
                            Toast.LENGTH_SHORT
                        ).show()
                        Observable.just(true).subscribe(AlbumDetActivity.observers)
                    }
                }
            })


    }
}

