package  com.example.xiaobai.music.music.model

import com.example.xiaobai.music.music.contract.SearchContract
import com.example.xiaobai.music.sql.bean.Search
import com.example.xiaobai.music.sql.dao.mSearchDao
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/19
 * @Description input description
 **/
class SearchModel : BaseModel(), SearchContract.IModel {
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
}