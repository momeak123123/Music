package  com.example.xiaobai.music.music.model

import android.content.Context
import android.widget.Toast
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.artistlist
import com.example.xiaobai.music.music.contract.SearchListContract
import com.example.xiaobai.music.music.view.act.RegisteredActivity
import com.example.xiaobai.music.music.view.act.SearchListActivity
import com.example.xiaobai.music.parsing.qqnusic
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/24
 * @Description input description
 **/
class SearchListModel : BaseModel(), SearchListContract.IModel {
    override fun qqdata(context: Context, search: String) {
        val musicall = mutableListOf<Music>()
        OkGo.get<String>("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.song&searchid=55196029248120147&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0&n=30&p=0")
            .params("w", search)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), qqnusic::class.javaObjectType)
                        if (bean.code == 0) {
                           val song = bean.data.getAsJsonObject("song")
                            val list = song.getAsJsonArray("list")
                          for(i in 0 until list.size()){

                                val music = list.get(i)
                                var jsonObj: JsonObject? = null
                                if (music.isJsonObject) {
                                    jsonObj = music.asJsonObject
                                }
                              val mid = jsonObj!!.get("mid").asString
                              val title = jsonObj.get("title").asString
                              val id = jsonObj.get("id").asLong
                              val album = jsonObj.get("album").asJsonObject
                              val album_id = album.get("id").asLong
                              val album_name = album.get("name").asString
                              val album_pmid = album.get("pmid").asString
                              val one = mutableListOf<artistlist>()
                              val singer = jsonObj.get("singer").asJsonArray
                              for(e in 0 until singer.size()) {
                                  val artist = singer.get(e)
                                  var jsonOs: JsonObject? = null
                                  if (artist.isJsonObject) {
                                      jsonOs = artist.asJsonObject
                                  }
                                  one.add(artistlist(jsonOs!!.get("id").asLong,jsonOs.get("name").asString))
                              }

                              musicall.add(Music(title, album_name, album_id, id, "", one, "http://y.gtimg.cn/music/photo_new/T002R300x300M000$album_pmid.jpg", 0, mid))
                            }

                            Observable.just(musicall).subscribe(SearchListActivity.observer)

                        } else {
                            Toast.makeText(
                                context,
                                bean.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                    }
                }
            })
    }
}

