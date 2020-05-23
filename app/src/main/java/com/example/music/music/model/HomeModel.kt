package  com.example.music.music.model

import com.example.music.bean.HomeList
import com.example.music.bean.HomeSinger
import com.example.music.music.contract.HomeContract
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class HomeModel : BaseModel(), HomeContract.IModel {
    var list = mutableListOf<BannerItem>()
    var data1 = mutableListOf<HomeList>()
    var data2 = mutableListOf<HomeList>()
    var data3 = mutableListOf<HomeSinger>()
    var data4 = mutableListOf<HomeList>()
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

    override fun getdata1(): MutableList<HomeList> {
        data1.clear()
        for (i in 0..5) {
            data1.add(HomeList("https://momeak.oss-cn-shenzhen.aliyuncs.com/dear1.png","Honey","Robuy"))
        }
        return data1
    }

    override fun getdata2(): MutableList<HomeList> {
        data2.clear()
        for (i in 0..5) {
            data2.add(HomeList("https://momeak.oss-cn-shenzhen.aliyuncs.com/dear2.png","Honey","Robuy"))
        }
        return data2
    }

    override fun getdata3(): MutableList<HomeSinger> {
        data3.clear()
        for (i in 0..7) {
            data3.add(HomeSinger("https://momeak.oss-cn-shenzhen.aliyuncs.com/dear3.png","Honey"))
        }
        return data3
    }

    override fun getdata4(): MutableList<HomeList> {
        data4.clear()
        for (i in 0..5) {
            data4.add(HomeList("https://momeak.oss-cn-shenzhen.aliyuncs.com/dear1.png","Honey","Robuy"))
        }
        return data4
    }

}