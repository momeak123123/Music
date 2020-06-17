package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.music.contract.UserEditContract
import com.example.music.music.presenter.UserEditPresenter
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.utils.ResUtils
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.user_edit.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class UserEditActivity : BaseMvpActivity<UserEditContract.IPresenter>(), UserEditContract.IView {

    companion object {

        lateinit var observer: Observer<Boolean>

    }

    private  var mSexOption = arrayOf("男","女")
    private var sexSelectOption = 1
    private lateinit var sp: SharedPreferences
    private lateinit var context: Context

    override fun registerPresenter() = UserEditPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.user_edit
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context =this
    }


    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }
        RxView.clicks(gender)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(btn_edit)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if (name.text.toString() != "") {
                    if (gender.text.toString() != "") {
                        if (city.text.toString() != "") {
                            if(mSexOption[0] == gender.text.toString()){
                                getPresenter().registerdata(
                                    context,
                                    name.text.toString(),
                                    1,
                                    city.text.toString(),
                                    sp.getString("url", "").toString()
                                )
                            }else{
                                getPresenter().registerdata(
                                    context,
                                    name.text.toString(),
                                    2,
                                    city.text.toString(),
                                    sp.getString("url", "").toString()
                                )
                            }



                        } else {
                            Toast.makeText(context, R.string.error_user, Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        Toast.makeText(context, R.string.error_user, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, R.string.error_user, Toast.LENGTH_SHORT).show()
                }
            }
    }




    override fun initData() {
        super.initData()

         sp =
            getSharedPreferences("User", Context.MODE_PRIVATE)
        Glide.with(context).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(ima)
        name.text = Editable.Factory.getInstance().newEditable(sp.getString("name", ""))
        gender.text = Editable.Factory.getInstance().newEditable(sp.getString("gender", ""))
        city.text = Editable.Factory.getInstance().newEditable(sp.getString("countries", ""))
    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Boolean) {
                if(data){
                   finish()
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}

