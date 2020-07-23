package com.app.xiaobai.music.music.presenter

import android.content.Context
import com.app.xiaobai.music.music.contract.StartPageContract
import com.app.xiaobai.music.music.model.StartPageModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class StartPagePresenter : BaseMvpPresenter<StartPageContract.IView, StartPageContract.IModel>(), StartPageContract.IPresenter{

    override fun registerModel() = StartPageModel::class.java
    override fun homedata(context: Context) {
        getModel().homedata(context)
    }

    override fun ads() {
        getModel().ads()
    }

}
