package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.MyContract
import com.example.music.music.model.MyModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class MyPresenter : BaseMvpPresenter<MyContract.IView, MyContract.IModel>(), MyContract.IPresenter{

    override fun registerModel() = MyModel::class.java

}
