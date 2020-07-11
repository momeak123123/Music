package  com.example.xiaobai.music.music.model

import android.content.Context
import android.widget.Toast
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.music.contract.SearchListContract
import com.example.xiaobai.music.music.view.act.RegisteredActivity
import com.example.xiaobai.music.music.view.act.SearchListActivity
import com.example.xiaobai.music.music.view.fragment.FindFragment
import com.example.xiaobai.music.parsing.*
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
    override fun qqdata(context: Context, search: String, limi: Int) {
        val musicall = mutableListOf<Searchs>()
        OkGo.get<String>("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.song&searchid=55196029248120147&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0&n=30")
            .params("p", limi)
            .params("w", search)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    if (response.code() == 200) {
                        try {
                            val bean =
                                Gson().fromJson(response.body(), qqmusic::class.javaObjectType)
                            if (bean.code == 0) {
                                val song = bean.data.getAsJsonObject("song")
                                val list = song.getAsJsonArray("list")
                                for (i in 0 until list.size()) {

                                    val music = list.get(i)
                                    var jsonObj: JsonObject? = null
                                    if (music.isJsonObject) {
                                        jsonObj = music.asJsonObject
                                    }
                                    val mid =
                                        "http://symusic.top/music.php?source=tencent&types=url&mid=" + jsonObj!!.get(
                                            "mid"
                                        ).asString + "&br=hq"
                                    val title = jsonObj.get("title").asString
                                    val id = jsonObj.get("id").asLong
                                    val album = jsonObj.get("album").asJsonObject
                                    val album_id = album.get("id").asLong
                                    val album_name = album.get("name").asString
                                    val album_pmid =
                                        "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + album.get(
                                            "pmid"
                                        ).asString + ".jpg"
                                    val one = mutableListOf<artistlist>()
                                    val singer = jsonObj.get("singer").asJsonArray
                                    for (e in 0 until singer.size()) {
                                        val artist = singer.get(e)
                                        var jsonOs: JsonObject? = null
                                        if (artist.isJsonObject) {
                                            jsonOs = artist.asJsonObject
                                        }
                                        one.add(
                                            artistlist(
                                                jsonOs!!.get("id").asLong,
                                                jsonOs.get("name").asString
                                            )
                                        )
                                    }

                                    musicall.add(
                                        Searchs(
                                            1,
                                            Music(
                                                title,
                                                album_name,
                                                album_id,
                                                id,
                                                "",
                                                one,
                                                album_pmid,
                                                0,
                                                mid
                                            )
                                        )

                                    )
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
                    } else {
                        Observable.just(true).subscribe(SearchListActivity.observers)
                    }
                }
            })
    }

    override fun kugoudata(context: Context, search: String, limi: Int) {
        val musicall = mutableListOf<Searchs>()
        OkGo.get<String>("http://songsearch.kugou.com/song_search_v2?keyword=$search&page=$limi&pagesize=50&userid=-1&clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    if (response.code() == 200) {
                        try {
                            val bean =
                                Gson().fromJson(response.body(), kugoumusic::class.javaObjectType)
                            if (bean.error_code == 0) {
                                val list = bean.data.getAsJsonArray("lists")

                                for (i in 0 until list.size()) {

                                    val music = list.get(i)
                                    var jsonObj: JsonObject? = null
                                    if (music.isJsonObject) {
                                        jsonObj = music.asJsonObject
                                    }
                                    val mid =
                                        "http://symusic.top/music.php?source=kugou&types=url&mid=" + jsonObj!!.get(
                                            "HQFileHash"
                                        ).asString + "&br=hq"
                                    val title = jsonObj.get("SongName").asString
                                    val id = jsonObj.get("Scid").asLong
                                    val album_id = jsonObj.get("Audioid").asLong
                                    val album_name = jsonObj.get("AlbumName").asString
                                    val album_pmid =
                                        "http://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                                    val one = mutableListOf<artistlist>()
                                    val singer = Gson().fromJson<Array<Long>>(
                                        jsonObj.get("SingerId").asJsonArray,
                                        Array<Long>::class.java
                                    ).toMutableList()

                                    val ca = jsonObj.get("SingerName").asString.substring(4)
                                    val da = ca.substring(0, ca.lastIndexOf('<'))
                                    one.add(artistlist(singer[0], da))


                                    musicall.add(
                                        Searchs(
                                            2, Music(
                                                title,
                                                album_name,
                                                album_id,
                                                id,
                                                "",
                                                one,
                                                album_pmid,
                                                0,
                                                mid
                                            )
                                        )

                                    )
                                }

                                Observable.just(musicall).subscribe(SearchListActivity.observer)

                            } else {
                                Toast.makeText(
                                    context,
                                    bean.error_msg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                        }
                    } else {
                        Observable.just(true).subscribe(SearchListActivity.observers)
                    }
                }
            })
    }

    override fun baidudata(context: Context, search: String) {
        val musicall = mutableListOf<Searchs>()
        OkGo.get<String>("http://tingapi.ting.baidu.com/v2/restserver/ting?format=json&method=baidu.ting.search.merge&page_size=50&query=$search&page_no=0")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), baidumusic::class.javaObjectType)
                        if (bean.error_code == 0) {
                            val list = bean.result.getAsJsonArray("song_info")
                            for (i in 0 until list.size()) {

                                val music = list.get(i)
                                var jsonObj: JsonObject? = null
                                if (music.isJsonObject) {
                                    jsonObj = music.asJsonObject
                                }
                                val mid =
                                    "http://symusic.top/music.php?source=baidu&types=url&mid=" + jsonObj!!.get(
                                        "ting_uid"
                                    ).asString + "&br=hq"
                                val title = jsonObj.get("title").asString
                                val id = jsonObj.get("song_id").asLong
                                val album_id = jsonObj.get("album_id").asLong
                                val album_name = jsonObj.get("album_title").asString
                                val album_pmid = jsonObj.get("pic_small").asString
                                val one = mutableListOf<artistlist>()
                                val srtist_id = jsonObj.get("all_artist_id").asString.split(",")
                                val artist_name = jsonObj.get("author").asString.split(",")
                                for (e in srtist_id.indices) {
                                    one.add(artistlist(srtist_id[e].toLong(), artist_name[e]))
                                }

                                musicall.add(
                                    Searchs(
                                        3, Music(
                                            title,
                                            album_name,
                                            album_id,
                                            id,
                                            "",
                                            one,
                                            album_pmid,
                                            0,
                                            mid
                                        )
                                    )

                                )
                            }

                            Observable.just(musicall).subscribe(SearchListActivity.observer)

                        }
                    } catch (e: Exception) {
                    }
                }
            })
    }

    override fun wangyidata(context: Context, search: String) {
        val musicall = mutableListOf<Searchs>()
        OkGo.get<String>("http://music.163.com/api/cloudsearch/pc?s=$search&limit=50&type=1&total=true&offset=0")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), wangyimusic::class.javaObjectType)
                        if (bean.code == 0) {
                            val list = bean.result.getAsJsonArray("songs")
                            for (i in 0 until list.size()) {

                                val music = list.get(i)
                                var jsonObj: JsonObject? = null
                                if (music.isJsonObject) {
                                    jsonObj = music.asJsonObject
                                }
                                val mid =
                                    "http://symusic.top/music.php?source=netease&types=url&mid=" + jsonObj!!.get(
                                        "id"
                                    ).asString + "&br=hq"
                                val title = jsonObj.get("name").asString
                                val id = jsonObj.get("id").asLong
                                val album = jsonObj.get("al").asJsonObject
                                val album_id = album.get("id").asLong
                                val album_name = album.get("name").asString
                                val album_pmid = album.get("picUrl").asString
                                val one = mutableListOf<artistlist>()
                                val singer = jsonObj.get("ar").asJsonArray
                                for (e in 0 until singer.size()) {
                                    val artist = singer.get(e)
                                    var jsonOs: JsonObject? = null
                                    if (artist.isJsonObject) {
                                        jsonOs = artist.asJsonObject
                                    }
                                    one.add(
                                        artistlist(
                                            jsonOs!!.get("id").asLong,
                                            jsonOs.get("name").asString
                                        )
                                    )
                                }

                                musicall.add(
                                    Searchs(
                                        4, Music(
                                            title,
                                            album_name,
                                            album_id,
                                            id,
                                            "",
                                            one,
                                            album_pmid,
                                            0,
                                            mid
                                        )
                                    )

                                )
                            }

                            Observable.just(musicall).subscribe(SearchListActivity.observer)

                        }
                    } catch (e: Exception) {
                    }
                }
            })
    }

    override fun kuwodata(context: Context, search: String) {
        val musicall = mutableListOf<Searchs>()
        OkGo.get<String>("http://search.kuwo.cn/r.s?client=kt&all=$search&pn=0&rn=50&uid=221260053&ver=kwplayer_ar_99.99.99.99&vipver=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    try {
                        val bean =
                            Gson().fromJson(response.body(), kuwomusic::class.javaObjectType)
                        if (bean.abslist.size() > 0) {
                            for (i in 0 until bean.abslist.size()) {

                                val music = bean.abslist.get(i)
                                var jsonObj: JsonObject? = null
                                if (music.isJsonObject) {
                                    jsonObj = music.asJsonObject
                                }
                                val mid =
                                    "http://symusic.top/music.php?source=kuwo&types=url&mid=" + jsonObj!!.get(
                                        "MUSICRID"
                                    ).asString + "&br=hq"
                                val title = jsonObj.get("SONGNAME").asString
                                val id = jsonObj.get("PAY").asLong
                                val album_id = jsonObj.get("ALBUMID").asLong
                                val album_name = jsonObj.get("ALBUM").asString
                                val album_pmid = ""
                                val one = mutableListOf<artistlist>()
                                one.add(
                                    artistlist(
                                        jsonObj.get("ARTISTID").asLong,
                                        jsonObj.get("ARTIST").asString
                                    )
                                )
                                musicall.add(
                                    Searchs(
                                        5, Music(
                                            title,
                                            album_name,
                                            album_id,
                                            id,
                                            "",
                                            one,
                                            album_pmid,
                                            0,
                                            mid
                                        )
                                    )

                                )
                            }
                            Observable.just(musicall).subscribe(SearchListActivity.observer)

                        }
                    } catch (e: Exception) {
                    }
                }
            })
    }


}

