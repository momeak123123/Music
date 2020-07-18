package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.config.Constants
import com.example.xiaobai.music.music.contract.HomeContract
import com.example.xiaobai.music.music.view.fragment.HomeFragment
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
        return list
    }

    override fun homedata(context: Context) {

        OkGo.get<String>(Constants.URL)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                        if (bean.code == 200) {

                            Observable.just(bean.data).subscribe(HomeFragment.observer)

                            val ads: List<Banner> = Gson().fromJson<Array<Banner>>(
                                bean.data.getAsJsonArray("ads"),
                                Array<Banner>::class.java
                            ).toList()

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

                            val str = Gson().toJson(ads)
                            val str1 = Gson().toJson(album)
                            val str2 = Gson().toJson(artist)
                            val str3 = Gson().toJson(song)
                            val str4 = Gson().toJson(list)

                            val sp: SharedPreferences =
                                context.getSharedPreferences("Music", Context.MODE_PRIVATE)

                            sp.edit().putString("ads", str).apply()
                            sp.edit().putString("album", str1).apply()
                            sp.edit().putString("artist", str2).apply()
                            sp.edit().putString("song", str3).apply()
                            sp.edit().putString("list", str4).apply()


                        } else {
                            Toast.makeText(
                                context,
                                bean.msg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {

                    }
                }
            })

    }

}

