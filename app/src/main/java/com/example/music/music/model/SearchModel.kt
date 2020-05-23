package  com.example.music.music.model

import com.example.music.bean.Music
import com.example.music.music.contract.SearchContract
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/19
 * @Description input description
 **/
class SearchModel : BaseModel(), SearchContract.IModel {
    val Datas = mutableListOf<String>()
    override fun listdata(): MutableList<String> {
       return Datas
    }
}