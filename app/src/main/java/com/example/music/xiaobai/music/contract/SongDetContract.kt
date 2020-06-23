package com.example.music.xiaobai.music.contract

import android.content.Context
import com.example.music.xiaobai.bean.Music
import com.example.music.xiaobai.sql.bean.Down
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
interface SongDetContract {

    interface IView : IViewContract {
    }

    interface IPresenter : IPresenterContract {
        fun listdata(context: Context, id: Long)
        fun deldata(context: Context, ids: Long,playids: Long)
        fun delsong(context: Context,  song: MutableList<Music>,playids: Long)
        fun delsongs(context: Context,  song: Music,data:Int,playids: Long)
    }

    interface IModel : IModelContract{
        fun listdata(context: Context,id: Long)
        fun deldata(context: Context, ids: Long,playids: Long)
        fun delsong(context: Context ,song: MutableList<Music>,playids: Long)
        fun delsongs(context: Context, song: Music,data:Int,playids: Long)
    }
}
