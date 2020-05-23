package com.example.music.music.view.fragment

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.R
import com.example.music.adapter.BottomMusicAdapter
import com.example.music.bean.Music
import com.example.music.music.contract.PlayControlContract
import com.example.music.music.presenter.PlayControlPresenter
import com.example.music.music.view.act.MusicPlaybackActivity
import kotlinx.android.synthetic.main.music_feugiat.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.ArrayList

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class PlayControlFragment : BaseMvpFragment<PlayControlContract.IPresenter>(), PlayControlContract.IView {

    companion object{
        var Datas = mutableListOf<Music>()
    }

    private var mAdapter: BottomMusicAdapter? = null
    private val musicList = ArrayList<Music>()
    private var item : Int? = -1

    override fun registerPresenter() = PlayControlPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.music_feugiat
    }

    override fun initView() {
        super.initView()
        updatePlayStatus(MusicPlaybackActivity.wlMusic.isPlaying)
        musicList.clear()
        musicList.addAll(getPresenter().getPlayList())
        initSongList()
    }



    private fun updatePlayStatus(isPlaying: Boolean) {
        if (isPlaying && !playPauseView.isPlaying) {
            playPauseView.play()
        } else if (!isPlaying && playPauseView.isPlaying) {
            playPauseView.pause()
        }
    }

    /**
     * 初始化歌曲列表
     */
    private fun initSongList() {
        if (mAdapter == null) {
            bottomPlayRcv.layoutManager = LinearLayoutManager(this.context!!)
            bottomPlayRcv.itemAnimator = DefaultItemAnimator()
            val adapter = BottomMusicAdapter(Datas,this.context!!)
            bottomPlayRcv.adapter = adapter
            adapter.setOnKotlinItemClickListener(object : BottomMusicAdapter.IKotlinItemClickListener {
                override fun onItemClickListener(position: Int) {
                     item = position
                }
            })
        } else {
            mAdapter?.notifyDataSetChanged()
        }
        bottomPlayRcv.scrollToPosition(MusicPlaybackActivity.position)
    }
}
