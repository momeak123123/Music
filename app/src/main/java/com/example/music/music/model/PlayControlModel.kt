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

        return Datas
    }
}