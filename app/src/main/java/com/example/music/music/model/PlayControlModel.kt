package  com.example.music.music.model

import com.example.music.bean.Music
import com.example.music.music.contract.PlayControlContract
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
 class PlayControlModel : BaseModel(), PlayControlContract.IModel {
    val Datas = mutableListOf<Music>()
    override fun getPlayList(): MutableList<Music> {
        Datas.clear()
        for (i in 1..3) {
            val music = Music("1")
            music.id = i.toLong()
            music.title="大鱼"
            music.uri = "http://mpge.5nd.com/2015/2015-11-26/69708/1.mp3"
            music.coverUri = "https://momeak.oss-cn-shenzhen.aliyuncs.com/dear$i.png"
            music.artist = "周深"
            music.albumId="1001"
            music.trackNumber = 3
            Datas.add(music)
        }
        return Datas
    }
}