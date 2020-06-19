package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.music.MusicApp
import com.example.music.R
import com.example.music.config.GlideEngine
import com.example.music.music.contract.UserEditContract
import com.example.music.music.presenter.UserEditPresenter
import com.jakewharton.rxbinding2.view.RxView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.artist_index.*
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

    private var picturePath: String? = null
    private var mSexOption = arrayOf("男", "女")
    private var sexSelectOption = 1
    private lateinit var sp: SharedPreferences
    private lateinit var context: Context

    override fun registerPresenter() = UserEditPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.user_edit
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
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

        RxView.clicks(ima)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                PictureSelector.create(context as UserEditActivity)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(true) // 是否显示拍照按钮
                    .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                    .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .circleDimmedLayer(false) // 是否圆形裁剪
                    .rotateEnabled(true) // 裁剪是否可旋转图片
                    .scaleEnabled(true) // 裁剪是否可放大缩小图片
                    .selectionMode(PictureConfig.SINGLE)
                    .cropImageWideHigh(1,1)
                    .forResult(PictureConfig.CHOOSE_REQUEST)
            }

        RxView.clicks(btn_edit)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if (MusicApp.getNetwork()) {
                    if (name.text.toString() != "") {
                        if (gender.text.toString() != "") {
                            if (city.text.toString() != "") {
                                if (mSexOption[0] == gender.text.toString()) {
                                    getPresenter().registerdata(
                                        context,
                                        name.text.toString(),
                                        1,
                                        city.text.toString(),
                                        sp.getString("url", "").toString()
                                    )
                                } else {
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
                } else {
                    if (swipe_refresh_layout != null) {
                        swipe_refresh_layout.isRefreshing = false
                    }
                    Toast.makeText(
                        context,
                        getText(R.string.nonet),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) { // 图片选择结果回调
                if (PictureSelector.obtainMultipleResult(data)[0].isCompressed) {
                    picturePath =
                        PictureSelector.obtainMultipleResult(data)[0].compressPath
                    Glide.with(context).load(picturePath).placeholder(R.color.main_black_grey).into(ima)
                }
            }
        }
    }


    override fun initData() {
        super.initData()

        sp =
            getSharedPreferences("User", Context.MODE_PRIVATE)
        Glide.with(context).load(sp.getString("url", "")).placeholder(R.color.main_black_grey)
            .into(ima)
        name.text = Editable.Factory.getInstance().newEditable(sp.getString("name", ""))
        gender.text = Editable.Factory.getInstance().newEditable(sp.getString("gender", ""))
        city.text = Editable.Factory.getInstance().newEditable(sp.getString("countries", ""))
    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Boolean) {
                if (data) {
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

