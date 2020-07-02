package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.FindContract
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

    override fun listcean(): MutableList<Search> {
        var Data = mutableListOf<Search>()
        val search = Search()
        search.txt = "周杰伦"
        search.state = 0
        Data.add(search)

        val search1 = Search()
        search1.txt = "林俊杰"
        search1.state = 0
        Data.add(search1)

        val search2 = Search()
        search2.txt = "华晨宇"
        search2.state = 0
        Data.add(search2)

        val search3 = Search()
        search3.txt = "Dance Monkey"
        search3.state = 0
        Data.add(search3)

        val search4 = Search()
        search4.txt = "独"
        search4.state = 0
        Data.add(search4)

        return Data
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
                        val da = ca.substring(0,ca.lastIndexOf('<'))
                        val bean =
                            Gson().fromJson(da, kugousearch::class.javaObjectType)
                             println( bean.data)
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