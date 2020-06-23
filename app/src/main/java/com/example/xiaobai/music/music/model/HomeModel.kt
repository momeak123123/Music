package  com.example.xiaobai.music.music.model

import android.content.Context
import com.example.xiaobai.music.music.contract.HomeContract
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
        return list
    }

    override fun homedata(context: Context) {



    }

}

