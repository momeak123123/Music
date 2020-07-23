package com.app.xiaobai.music.music.presenter

import android.content.Context
import com.app.xiaobai.music.music.contract.UserEditContract
import com.app.xiaobai.music.music.model.UserEditModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class UserEditPresenter : BaseMvpPresenter<UserEditContract.IView, UserEditContract.IModel>(), UserEditContract.IPresenter{

    override fun registerModel() = UserEditModel::class.java

    override fun registerdata(context: Context, name: String, gender: Int, city: String, images: String,mess:String) {
      getModel().registerdata(context,name,gender,city,images,mess)
    }

    override fun osst(context: Context, picturePath: String) {
        getModel().osst(context, picturePath)
    }


}
