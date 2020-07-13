package com.example.xiaobai.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.SongDet
import com.example.xiaobai.music.config.CornerTransform
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.example.xiaobai.music.music.view.act.SongDetActivity
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.utils.ResUtils
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class SongDetAdapter(
    val datas: MutableList<Music>, val context: Context,
    val covers: String,
    val id: Long,
    val names: String, var num: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        const val TYPE_TITLE = 0
        const val TYPE_SELLER = 1
    }

    var type = 0
    private var mItemClickListener: ItemClickListener? = null
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
        return when (position) {
            TYPE_TITLE -> TitleHolder(
                LayoutInflater.from(context).inflate(R.layout.song_index_header, holder, false)
            )
            TYPE_SELLER -> SellerHolder(
                LayoutInflater.from(context).inflate(R.layout.song_index_item, holder, false)
            )
            else -> TitleHolder(
                LayoutInflater.from(context).inflate(R.layout.song_index_header, holder, false)
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
        var title: TextView
        var top_set: ImageView
        var pre: ImageView
        var play: ImageView
        var next: ImageView

        init {
            iv_cover = itemView.findViewById(R.id.iv_cover)
            txt = itemView.findViewById(R.id.txt)
            top_flot = itemView.findViewById(R.id.top_flot)
            title = itemView.findViewById(R.id.title)
            top_set = itemView.findViewById(R.id.top_set)
            pre = itemView.findViewById(R.id.pre)
            play = itemView.findViewById(R.id.play)
            next = itemView.findViewById(R.id.next)
        }

        fun dip2px(context: Context, dpValue: Int): Float {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f)
        }

        @SuppressLint("CheckResult")
        fun bindData() {
            val transformation = CornerTransform(context, dip2px(context, 40))
            transformation.setExceptCorner(true, true, false, false)
            Glide.with(context).load(covers).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(transformation).into(iv_cover)

            // Glide.with(context).load(covers).placeholder(R.drawable.undetback).into(iv_cover)
            title.text = names
            txt.text = num + "首"
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
                    Observable.just(true).subscribe(SongDetActivity.observers)
                }

            RxView.clicks(top_set)
                .throttleFirst(0, TimeUnit.SECONDS)
                .subscribe {
                    if (type == 0) {
                        type = 1
                        notifyItemRangeChanged(1, datas.size)
                        Observable.just(1).subscribe(SongDetActivity.observerd)
                    } else {
                        type = 0
                        notifyItemRangeChanged(1, datas.size)
                        Observable.just(0).subscribe(SongDetActivity.observerd)
                    }

                }

            RxView.clicks(pre)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (MusicPlayActivity.bool) {
                        if (MusicApp.getAblumid() == id) {
                            Observable.just(1).subscribe(MusicPlayActivity.observerset)
                        }
                    } else {
                        Observable.just(false).subscribe(SongDetActivity.observers)
                    }

                }
            RxView.clicks(play)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (MusicPlayActivity.bool) {
                        if (MusicApp.getAblumid() == id) {
                            try {
                                if (MusicApp.getPlay()) {
                                    Observable.just(0).subscribe(MusicPlayActivity.observerset)
                                    play.setImageResource(R.drawable.play)
                                } else {
                                    Observable.just(3).subscribe(MusicPlayActivity.observerset)
                                    play.setImageResource(R.drawable.plays)
                                }

                            } catch (e: Exception) {
                                Observable.just(false).subscribe(SongDetActivity.observers)
                            }


                        } else {
                            Observable.just(false).subscribe(SongDetActivity.observers)
                            play.setImageResource(R.drawable.plays)
                        }
                    } else {
                        Observable.just(false).subscribe(SongDetActivity.observers)
                        play.setImageResource(R.drawable.plays)

                    }

                }
            RxView.clicks(next)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (MusicPlayActivity.bool) {
                        if (MusicApp.getAblumid() == id) {
                            Observable.just(2).subscribe(MusicPlayActivity.observerset)
                        }
                    } else {
                        Observable.just(false).subscribe(SongDetActivity.observers)
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
        var main: RelativeLayout
        var delete: Button

        init {
            iv_cover = itemView.findViewById(R.id.iv_cover)
            title = itemView.findViewById(R.id.title)
            txt = itemView.findViewById(R.id.txt)
            more = itemView.findViewById(R.id.more)
            radio = itemView.findViewById(R.id.radio)
            num = itemView.findViewById(R.id.num)
            main = itemView.findViewById(R.id.main)
            delete = itemView.findViewById(R.id.delete)
        }

        fun bindData(position: Int) {

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

            main.setOnClickListener {
                mItemClickListener?.onItemClick(position)
            }

            more.setOnClickListener {
                Observable.just(position).subscribe(SongDetActivity.observert)
            }

            delete.setOnClickListener {
                Observable.just(position).subscribe(SongDetActivity.observerdel)
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
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }


    fun add(item: Music) {
        datas.add(item)
        notifyItemChanged(datas.size)
    }

    fun remove(position: Int) {
        notifyItemRemoved(position)
        if (position != datas.size) {
            notifyItemRangeChanged(position, datas.size - position);
        }
    }

    fun removedata(item: Music) {
        datas.remove(item)
    }
}