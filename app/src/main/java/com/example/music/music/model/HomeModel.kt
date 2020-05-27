package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.ResultBean
import com.example.music.common.Constants
import com.example.music.music.contract.HomeContract
import com.google.gson.GsonBuilder
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

    override fun listdata(context: Context) {
        OkGo.get<String>(Constants.URL + "api")
            .execute(object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                /**
                 * 成功回调
                 */
                val gb: GsonBuilder? = GsonBuilder()
                gb!!.setPrettyPrinting().disableHtmlEscaping()

                val bean =
                    gb.create().fromJson(response.body(), ResultBean::class.javaObjectType)
                if (bean.code == 200) {

                   /* val album:List<Album> =  Gson().fromJson<Array<Album>>(bean.data.get("album_list"), Array<Album>::class.java).toList()
                    val artist:List<Artists> =  Gson().fromJson<Array<Artists>>(bean.data.get("hot_artist"), Array<Artists>::class.java).toList()
                    val song:List<Song> =  Gson().fromJson<Array<Song>>(bean.data.get("hot_song"), Array<Song>::class.java).toList()
                    val list:List<TopList> =  Gson().fromJson<Array<TopList>>(bean.data.get("top_list"), Array<TopList>::class.java).toList()
                    val str1 = gson.toJson(album)
                    val str2 = gson.toJson(artist)
                    val str3 = gson.toJson(song)
                    val str4 = gson.toJson(list)*/
                    val sp: SharedPreferences =context.getSharedPreferences("Music", Context.MODE_PRIVATE)

                    sp.edit().putString("album", bean.data.get("album_list").asJsonObject.asString).apply()
                    sp.edit().putString("artist", bean.data.get("hot_artist").asJsonObject.asString).apply()
                    sp.edit().putString("song", bean.data.get("hot_song").asJsonObject.asString).apply()
                    sp.edit().putString("list", bean.data.get("top_list").asJsonObject.asString).apply()
                } else {
                    Toast.makeText(
                        context,
                        bean.data.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }



}

