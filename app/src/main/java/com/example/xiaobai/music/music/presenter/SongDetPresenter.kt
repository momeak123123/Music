package com.example.xiaobai.music.music.presenter

import android.content.Context
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.music.contract.SongDetContract
import com.example.xiaobai.music.music.model.SongDetModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class SongDetPresenter : BaseMvpPresenter<SongDetContract.IView, SongDetContract.IModel>(), SongDetContract.IPresenter{

    override fun registerModel() = SongDetModel::class.java
    override fun listdata(context: Context, id: Long) {
        getModel().listdata(context,id)
    }

    override fun deldata(context: Context, ids: Long,playids: Long) {
        getModel().deldata(context,ids,playids)
    }

    override fun delsong(context: Context,  song: MutableList<Music>,playids: Long) {
        getModel().delsong(context,song,playids)
    }

    override fun delsongs(context: Context,  song: Music,data:Int,playids: Long) {
        getModel().delsongs(context,song,data,playids)
    }

}
