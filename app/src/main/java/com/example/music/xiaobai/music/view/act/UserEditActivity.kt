package com.example.music.xiaobai.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.music.xiaobai.MusicApp
import com.example.music.xiaobai.R
import com.example.music.xiaobai.adapter.GridImageAdapter
import com.example.music.xiaobai.config.GlideEngine
import com.example.music.xiaobai.music.contract.UserEditContract
import com.example.music.xiaobai.music.presenter.UserEditPresenter
import com.jakewharton.rxbinding2.view.RxView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.user_edit.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class UserEditActivity : BaseMvpActivity<UserEditContract.IPresenter>(), UserEditContract.IView {

    companion object {

        lateinit var observer: Observer<Boolean>
        lateinit var observers: Observer<LocalMedia>
    }

    private val mAdapter: GridImageAdapter? = null
    private var picturePath: String? = ""
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
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(gender)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(ima)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                PictureSelector.create(context as UserEditActivity)
                    .openGallery(PictureMimeType.ofImage())
                    .imageEngine(GlideEngine.createGlideEngine())
                    .isWeChatStyle(true)
                    .isEnableCrop(true)// 是否裁剪
                    .isCompress(true)// 是否压缩
                    .isCamera(true) // 是否显示拍照按钮
                    .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                    .showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(true) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .circleDimmedLayer(false) // 是否圆形裁剪
                    .rotateEnabled(true) // 裁剪是否可旋转图片
                    .scaleEnabled(true) // 裁剪是否可放大缩小图片
                    .selectionMode(PictureConfig.SINGLE)
                    .isSingleDirectReturn(true)
                    .withAspectRatio(1,1)
                    .cutOutQuality(90)// 裁剪输出质量 默认100
                    .minimumCompressSize(100)// 小于多少kb的图片不压缩
                    .forResult(MyResultCallback(mAdapter))
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

  /*  override fun onActivityResult(
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
                   // Glide.with(context).load(picturePath).placeholder(R.color.main_black_grey).into(ima)
                }
            }
        }
    }*/

    /**
     * 返回结果回调
     */
     class MyResultCallback(adapter: GridImageAdapter?) :
        OnResultCallbackListener<LocalMedia> {
        private val mAdapterWeakReference: WeakReference<GridImageAdapter?> = WeakReference<GridImageAdapter?>(adapter)
        override fun onResult(result: List<LocalMedia>) {
            for (media in result) {
                if (media.isCompressed) {
                    Observable.just(media).subscribe(observers)
                }
            }
            if (mAdapterWeakReference.get() != null) {
                mAdapterWeakReference.get()!!.setList(result)
                mAdapterWeakReference.get()!!.notifyDataSetChanged()
            }
        }

        override fun onCancel() {

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

        observers = object : Observer<LocalMedia> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(media: LocalMedia) {
                picturePath = media.compressPath
                if(picturePath.equals("")){
                    Toast.makeText(
                        context,
                        getText(R.string.title_notifications),
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    Glide.with(context).load(picturePath).placeholder(R.color.main_black_grey).into(ima)
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

