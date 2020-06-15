package com.example.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.bean.Music
import com.example.music.bean.SongDet
import com.example.music.music.view.act.AlbumDetActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.album_index_header.*
import java.util.concurrent.TimeUnit


class AlbumDetAdapter(
    val datas: MutableList<Music>,
    val context: Context,
    val txts: String,
    val covers: String,
    val names:String
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
        var floatingActionButton: FloatingActionButton

        init {
            iv_cover = itemView.findViewById(R.id.iv_cover)
            txt = itemView.findViewById(R.id.txt)
            top_flot = itemView.findViewById(R.id.top_flot)
            top_title = itemView.findViewById(R.id.top_title)
            top_set = itemView.findViewById(R.id.top_set)
            floatingActionButton = itemView.findViewById(R.id.floatingActionButton)
        }

        @SuppressLint("CheckResult")
        fun bindData() {
            txt.text = txts
            Glide.with(context).load(covers).placeholder(R.color.main_black_grey).into(iv_cover)
            top_title.text = names
            Glide.with(context).load(R.drawable.mores).placeholder(R.color.main_black_grey).into(top_set)
            floatingActionButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#06b7ff"))
            RxView.clicks(top_flot)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    Observable.just(true).subscribe(AlbumDetActivity.observers)
                }

            RxView.clicks(top_set)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (type == 0) {
                        type = 1
                        notifyDataSetChanged()
                    } else {
                        type = 0
                        notifyDataSetChanged()
                    }

                }


        }

    }

    inner class SellerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_cover: ImageView
        var title: TextView
        var txt: TextView
        var more: ImageView
        var radio: ImageView
        var num: TextView

        init {
            iv_cover = itemView.findViewById(R.id.iv_cover)
            title = itemView.findViewById(R.id.title)
            txt = itemView.findViewById(R.id.txt)
            more = itemView.findViewById(R.id.more)
            radio = itemView.findViewById(R.id.radio)
            num = itemView.findViewById(R.id.num)
        }

        fun bindData(position: Int) {

            itemView.setOnClickListener { v ->
                mItemClickListener?.onItemClick(v,position)
            }
            Glide.with(context).load(datas[position].pic_url).placeholder(R.color.main_black_grey)
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
                for (it in datas) {
                    listdet.add(SongDet(it, 0))
                }
            }

            radio.setOnClickListener {
                if (listdet[position].type == 0) {
                    listdet[position].type = 1
                    Glide.with(context).load(R.drawable.select).into(radio)
                } else {
                    listdet[position].type = 0
                    Glide.with(context).load(R.drawable.upselect).into(radio)
                }

            }

            more.setOnClickListener {

            }

        }
    }

    interface ItemClickListener {
        fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }
}

