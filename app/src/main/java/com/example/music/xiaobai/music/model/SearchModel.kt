package  com.example.music.xiaobai.music.model

import com.example.music.xiaobai.music.contract.SearchContract
import com.example.music.xiaobai.sql.bean.Search
import com.example.music.xiaobai.sql.dao.mSearchDao
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
}