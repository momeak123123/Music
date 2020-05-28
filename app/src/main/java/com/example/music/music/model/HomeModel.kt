package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.*
import com.example.music.common.Constants
import com.example.music.music.contract.HomeContract
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import mvp.ljb.kt.model.BaseModel


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class HomeModel : BaseModel(), HomeContract.IModel {
    var list = mutableListOf<BannerItem>()

    override fun imagesdata(): MutableList<BannerItem> {
        list.clear()
        val item1 = BannerItem()
        item1.imgUrl = "https://img.zcool.cn/community/011ad05e27a173a801216518a5c505.jpg"
        item1.title = ""
        list.add(item1)
        val item2 = BannerItem()
        item2.imgUrl = "https://img.zcool.cn/community/0148fc5e27a173a8012165184aad81.jpg"
        item2.title = ""
        list.add(item2)
        val item3 = BannerItem()
        item3.imgUrl = "https://img.zcool.cn/community/013c7d5e27a174a80121651816e521.jpg"
        item3.title = ""
        list.add(item3)
        return list
    }

}

