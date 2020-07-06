package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.FindContract
import com.example.xiaobai.music.music.view.act.AlbumDetActivity
import com.example.xiaobai.music.music.view.act.ArtistActivity
import com.example.xiaobai.music.music.view.fragment.FindFragment
import com.example.xiaobai.music.music.view.fragment.MyFragment
import com.example.xiaobai.music.parsing.kugouseBean
import com.example.xiaobai.music.parsing.kugousearch
import com.example.xiaobai.music.sql.bean.Search
import com.example.xiaobai.music.sql.dao.mSearchDao
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

    override fun listdata(): MutableList<Search> {
        return mSearchDao.queryAll()
    }

    override fun listcean(){
        OkGo.get<String>(Constants.URL + "api/Serach/index")
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

                                val sear: List<Sear> = Gson().fromJson<Array<Sear>>(
                                    bean.data,
                                    Array<Sear>::class.java
                                ).toList()
                                Observable.just(true).subscribe(FindFragment.observers)
                                Observable.just(sear).subscribe(FindFragment.observert)
                            }
                        } catch (e: Exception) {

                        }
                    }else{
                        Observable.just(false).subscribe(FindFragment.observers)
                    }



                }
            })
    }

    override fun search(queryText: String) {
        OkGo.get<String>("http://mobilecdngz.kugou.com/new/app/i/search.php")
            .params("keyword", queryText)
            .params("cmd", 302)
            .params("with_res_tag", 1)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */

                        try {

                            val ca = response.body().substring(23)
                            val da = ca.substring(0, ca.lastIndexOf('<'))
                            val bean =
                                Gson().fromJson(da, kugousearch::class.javaObjectType)
                            val ads: MutableList<kugouseBean> = Gson().fromJson<Array<kugouseBean>>(
                                bean.data,
                                Array<kugouseBean>::class.java
                            ).toMutableList()

                            Observable.just(ads).subscribe(FindFragment.observer)
                        } catch (e: Exception) {

                        }
                }
            })
    }
}