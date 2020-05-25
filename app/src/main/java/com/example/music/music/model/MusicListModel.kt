package  com.example.music.music.model

import com.example.music.bean.Music
import com.example.music.music.contract.MusicListContract
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/15
 * @Description input description
 **/
class MusicListModel : BaseModel(), MusicListContract.IModel {
    companion object {
        val Datas = mutableListOf<Music>()
    }

    override fun listdata(): MutableList<Music> {
        Datas.clear()

        val music = Music("net")//locat
        music.id = 0
        music.title = "大鱼"
        music.uri = "http://mpge.5nd.com/2015/2015-11-26/69708/1.mp3"
        music.coverUri = "https://momeak.oss-cn-shenzhen.aliyuncs.com/dear1.png"
        music.coverBig = "https://momeak.oss-cn-shenzhen.aliyuncs.com/dear1.png"
        music.artist = "周深"
        music.albumId = "1000"
        music.trackNumber = 3
        Datas.add(music)

        val music1 = Music("net")
        music1.id = 1
        music1.title = "大鱼"
        music1.uri = "https://momeak.oss-cn-shenzhen.aliyuncs.com/music.mp3"
        music1.coverUri = "https://momeak.oss-cn-shenzhen.aliyuncs.com/dear2.png"
        music1.coverBig = "https://momeak.oss-cn-shenzhen.aliyuncs.com/dear2.png"
        music1.artist = "周深"
        music1.albumId = "1001"
        music1.trackNumber = 3
        Datas.add(music1)

        val music2 = Music("net")
        music2.id = 2
        music2.title = "大鱼"
        music2.uri = "http://music.163.com/song/media/outer/url?id=1327199767.mp3"
        music2.coverUri = "https://momeak.oss-cn-shenzhen.aliyuncs.com/dear3.png"
        music2.coverBig = "https://momeak.oss-cn-shenzhen.aliyuncs.com/dear3.png"
        music2.artist = "周深"
        music2.albumId = "1002"
        music2.trackNumber = 3
        Datas.add(music2)



        return Datas
    }

}
