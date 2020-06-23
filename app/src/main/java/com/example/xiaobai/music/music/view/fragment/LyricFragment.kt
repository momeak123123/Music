package com.example.xiaobai.music.music.view.fragment

import android.os.Handler
import android.widget.Toast
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.ResultBean
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.LyricContract
import com.example.xiaobai.music.music.presenter.LyricPresenter
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import kotlinx.android.synthetic.main.frag_player_lrcview.*
import me.wcy.lrcview.LrcView
import mvp.ljb.kt.fragment.BaseMvpFragment

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LyricFragment : BaseMvpFragment<LyricContract.IPresenter>(), LyricContract.IView {


    private val handler = Handler()

    override fun registerPresenter() = LyricPresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.frag_player_lrcview
    }

    override fun onResume() {
        super.onResume()
        lrcView.setDraggable(true, LrcView.OnPlayClickListener { time: Long ->
            MusicPlayActivity.wlMusic.seek((time/1000).toInt(), true, true)
            if (!MusicPlayActivity.wlMusic.isPlaying) {
                MusicPlayActivity.wlMusic.start()
            }
            Observable.just(time/1000).subscribe(MusicPlayActivity.observers)

            true
        })
    }

     fun lrcView(songid:Long) {
        super.initView()

            OkGo.get<String>(Constants.URL + "api/index/get_lrclink")
                .params("song_id",songid)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        /**
                         * 成功回调
                         */
                        try {
                            val bean =
                                Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                            if (bean.code == 200) {
                                val lrclink = bean.data.get("lrclink").asString
                                // 加载歌词文本
                                lrcView.loadLrc("")
                                lrcView.loadLrc(lrclink)

                            } else {
                                Toast.makeText(
                                    context,
                                    bean.data.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                        }

                    }
                })
    }
}
