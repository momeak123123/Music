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
}