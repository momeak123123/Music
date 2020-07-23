package  com.app.xiaobai.music.music.model

import android.content.Context
import android.widget.Toast
import com.app.xiaobai.music.R
import com.app.xiaobai.music.bean.ResultBean
import com.app.xiaobai.music.config.Constants
import com.app.xiaobai.music.music.contract.ArtistDetContract
import com.app.xiaobai.music.music.view.act.ArtistDetActivity
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
class ArtistDetModel : BaseModel(), ArtistDetContract.IModel {
    override fun listdata(context: Context, id: Long, type: Int) {


        OkGo.get<String>(Constants.URL + "artist/get_artist_info")
            .params("artist_id", id)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    if (response.code() == 200) {
                        try {
                            val bean =
                                Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                            if (bean.code == 200) {
                                Observable.just(bean.data).subscribe(ArtistDetActivity.observer)
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
                        Observable.just(true).subscribe(ArtistDetActivity.observers)
                    }
                }
            })


    }
}