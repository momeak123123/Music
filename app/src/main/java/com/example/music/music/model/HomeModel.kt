package  com.example.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.music.bean.*
import com.example.music.common.Constants
import com.example.music.music.contract.HomeContract
import com.example.music.music.view.act.AlbumDetActivity
import com.example.music.music.view.fragment.HomeFragment
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import io.reactivex.Observable
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

    override fun homedata(context: Context) {


        OkGo.get<String>(Constants.URL + "api")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                        if (bean.code == 200) {

                            val album: List<Album> = Gson().fromJson<Array<Album>>(
                                bean.data.getAsJsonArray("album_list"),
                                Array<Album>::class.java
                            ).toList()
                            val artist: List<Artists> = Gson().fromJson<Array<Artists>>(
                                bean.data.getAsJsonArray("hot_artist"),
                                Array<Artists>::class.java
                            ).toList()
                            val song: List<Music> = Gson().fromJson<Array<Music>>(
                                bean.data.getAsJsonArray("hot_song"),
                                Array<Music>::class.java
                            ).toList()
                            val list: List<TopList> = Gson().fromJson<Array<TopList>>(
                                bean.data.getAsJsonArray("top_list"),
                                Array<TopList>::class.java
                            ).toList()
                            val str1 = Gson().toJson(album)
                            val str2 = Gson().toJson(artist)
                            val str3 = Gson().toJson(song)
                            val str4 = Gson().toJson(list)

                            val sp: SharedPreferences =
                                context.getSharedPreferences("Music", Context.MODE_PRIVATE)

                            sp.edit().putString("album", str1).apply()
                            sp.edit().putString("artist", str2).apply()
                            sp.edit().putString("song", str3).apply()
                            sp.edit().putString("list", str4).apply()


                        } else {
                            Toast.makeText(
                                context,
                                bean.data.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "程序出现了未知异常",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })


    }

}

