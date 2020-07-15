package com.example.xiaobai.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.SongDet
import com.example.xiaobai.music.music.view.act.AlbumDetActivity
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class AlbumDetAdapter(
    val datas: MutableList<Music>,
    val context: Context,
    val id: Long,
    val txts: String,
    val covers: String,
    val names: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_TITLE = 0
        const val TYPE_SELLER = 1
    }

    private var mItemClickListener: ItemClickListener? = null

    var type = 0

    var listdet = mutableListOf<SongDet>()

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_TITLE
        } else {
            TYPE_SELLER
        }
    }

    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): RecyclerView.ViewHolder {
        for (i in 0 until datas.size) {
            listdet.add(SongDet(datas[i], i,0))
        }
        //加载View
        return when (position) {
            TYPE_TITLE -> TitleHolder(
                LayoutInflater.from(context).inflate(R.layout.album_index_header, holder, false)
            )
            TYPE_SELLER -> SellerHolder(
                LayoutInflater.from(context).inflate(R.layout.album_index_item, holder, false)
            )
            else -> TitleHolder(
                LayoutInflater.from(context).inflate(R.layout.album_index_header, holder, false)
            )
        }

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int {

        return datas.size

    }


    /**
     * 绑定数据，View和数据绑定
     */

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < datas.size) {
            when (getItemViewType(position)) {
                TYPE_TITLE -> (holder as TitleHolder).bindData()
                TYPE_SELLER -> (holder as SellerHolder).bindData(position)
            }

        }


    }


    inner class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var iv_cover: ImageView
        var txt: TextView
        var top_flot: ImageView
        var top_title: TextView
        var top_set: ImageView
        var pre: ImageView
        var play: ImageView
        var next: ImageView

        init {
            iv_cover = itemView.findViewById(R.id.iv_cover)
            txt = itemView.findViewById(R.id.txt)
            top_flot = itemView.findViewById(R.id.top_flot)
            top_title = itemView.findViewById(R.id.top_title)
            top_set = itemView.findViewById(R.id.top_set)
            pre = itemView.findViewById(R.id.pre)
            play = itemView.findViewById(R.id.play)
            next = itemView.findViewById(R.id.next)
        }

        @SuppressLint("CheckResult")
        fun bindData() {
            txt.text = txts

            Glide.with(context).load(covers).placeholder(R.drawable.undetback).into(iv_cover)
            top_title.text = names
            top_set.setImageResource(R.drawable.mores)
            pre.setImageResource(R.drawable.shang)


            if (MusicApp.getAblumid() == id) {

                if (MusicApp.getPlay()) {
                    play.setImageResource(R.drawable.plays)
                } else {
                    play.setImageResource(R.drawable.play)
                }

            } else {
                play.setImageResource(R.drawable.play)
            }
            next.setImageResource(R.drawable.xia)

            RxView.clicks(top_flot)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    Observable.just(true).subscribe(AlbumDetActivity.observers)
                }

            RxView.clicks(top_set)
                .throttleFirst(0, TimeUnit.SECONDS)
                .subscribe {
                    if (type == 0) {
                        type = 1
                        notifyItemRangeChanged(1, datas.size)
                        Observable.just(1).subscribe(AlbumDetActivity.observerd)
                    } else {
                        type = 0
                        notifyItemRangeChanged(1, datas.size)
                        Observable.just(0).subscribe(AlbumDetActivity.observerd)
                    }

                }

            RxView.clicks(pre)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (MusicApp.getBool()) {
                        if (MusicApp.getAblumid() == id) {
                            Observable.just(1).subscribe(MusicPlayActivity.observerset)
                        }
                    } else {
                        Observable.just(false).subscribe(AlbumDetActivity.observers)
                    }

                }
            RxView.clicks(play)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (MusicApp.getBool()) {
                        if (MusicApp.getAblumid() == id) {
                            if (MusicApp.getPlay()) {
                                Observable.just(0).subscribe(MusicPlayActivity.observerset)
                                play.setImageResource(R.drawable.play)
                            } else {
                                Observable.just(3).subscribe(MusicPlayActivity.observerset)
                                play.setImageResource(R.drawable.plays)
                            }

                        } else {
                            Observable.just(false).subscribe(AlbumDetActivity.observers)
                            play.setImageResource(R.drawable.plays)
                        }
                    } else {
                        Observable.just(false).subscribe(AlbumDetActivity.observers)
                        play.setImageResource(R.drawable.plays)
                    }

                }
            RxView.clicks(next)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (MusicApp.getBool()) {
                        if (MusicApp.getAblumid() == id) {
                            Observable.just(2).subscribe(MusicPlayActivity.observerset)
                        }
                    } else {
                        Observable.just(false).subscribe(AlbumDetActivity.observers)
                    }


                }
        }

    }

    inner class SellerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_cover: ImageView
        var title: TextView
        var txt: TextView
        var more: ImageView
        var radio: RadioButton
        var num: TextView

        init {
            iv_cover = itemView.findViewById(R.id.iv_cover)
            title = itemView.findViewById(R.id.title)
            txt = itemView.findViewById(R.id.txt)
            more = itemView.findViewById(R.id.more)
            radio = itemView.findViewById(R.id.radio)
            num = itemView.findViewById(R.id.num)
        }

        @SuppressLint("ResourceAsColor")
        fun bindData(position: Int) {

            itemView.setOnClickListener { v ->
                mItemClickListener?.onItemClick(v, position)
            }



            Glide.with(context).load(datas[position].pic_url).placeholder(R.drawable.undetback)
                .into(iv_cover)
            title.text = datas[position].name
            val artist = datas[position].all_artist
            var srtist_name = ""
            for (it in artist) {
                if (srtist_name != "") {
                    srtist_name += "/" + it.name
                } else {
                    srtist_name = it.name
                }

            }
            txt.text = srtist_name

            if (type == 0) {
                num.visibility = View.VISIBLE
                radio.visibility = View.GONE
                num.text = (position).toString()
            } else {
                num.visibility = View.GONE
                radio.visibility = View.VISIBLE
                radio.isChecked = listdet[position].type == 1
            }


            more.setOnClickListener {
                Observable.just(position).subscribe(AlbumDetActivity.observert)
            }

        }
    }

    fun update(bool: Boolean) {
        if (bool) {
            listdet.clear()
            for (i in 0 until datas.size) {
                listdet.add(SongDet(datas[i], i,1))
            }
        } else {
            listdet.clear()
            for (i in 0 until datas.size) {
                listdet.add(SongDet(datas[i], i,0))
            }
        }
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }
}

