package com.example.music.music.view.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.adapter.HomeAlbumAdapter
import com.example.music.adapter.HomeListAdapter
import com.example.music.adapter.MusicListAdapter
import com.example.music.bean.Music
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.MusicListContract
import com.example.music.music.presenter.MusicListPresenter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.music_list.*
import mvp.ljb.kt.act.BaseMvpActivity


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/15
 * @Description input description
 **/
class MusicListActivity : BaseMvpActivity<MusicListContract.IPresenter>() , MusicListContract.IView {

    private lateinit var context: Context
    var Datas = mutableListOf<Music>()


    override fun registerPresenter() = MusicListPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.music_list
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context=this
    }

    override fun initData() {
        super.initData()
    }

    override fun initView() {
        super.initView()
        Datas.clear()
        Datas.addAll(getPresenter().listdata())
        recycleview()
    }

    fun recycleview(){

        musiclist.layoutManager = LinearLayoutManager(this)
        musiclist.itemAnimator = DefaultItemAnimator()
        val adapter = MusicListAdapter(Datas,this)
        musiclist.adapter = adapter
        musiclist.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val intent = Intent()
                        context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                        intent.putExtra("music",Datas[position])
                        startActivity(intent)
                        //Observable.just(Datas).subscribe(MusicPlayActivity.observer)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
