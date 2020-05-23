package com.example.music.music.presenter

import com.example.music.bean.Music
import com.example.music.music.contract.MusicListContract
import com.example.music.music.model.MusicListModel
import com.example.music.music.view.act.MusicPlaybackActivity
import com.example.music.music.view.fragment.PlayControlFragment
import io.reactivex.Observable
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/15
 * @Description input description
 **/
class MusicListPresenter : BaseMvpPresenter<MusicListContract.IView, MusicListContract.IModel>(), MusicListContract.IPresenter{

    override fun registerModel() = MusicListModel::class.java
    override fun listdata(): MutableList<Music> {
       return getModel().listdata()
    }

    override fun onclick(datas: MutableList<Music>, position: Int) {
        if(MusicListModel.Datas[0].albumId.equals(datas[0].albumId)){
            val observable = Observable.defer { Observable.just(MusicListModel.Datas[position].uri) }
            observable.subscribe(MusicPlaybackActivity.observernext)
        }else{
            PlayControlFragment.Datas.clear()
            PlayControlFragment.Datas.addAll(datas)
            val observables = Observable.defer { Observable.just(datas[position]) }
            observables.subscribe(MusicPlaybackActivity.observer)
        }

    }

}

