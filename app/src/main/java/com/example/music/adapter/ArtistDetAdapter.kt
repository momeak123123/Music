package com.example.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.bean.Album
import com.example.music.bean.AlbumDet
import com.example.music.bean.Artists
import com.example.music.bean.SongDet
import com.example.music.music.view.act.AlbumDetActivity
import com.example.music.music.view.act.ArtistDetActivity
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ArtistDetAdapter  (val datas: List<Album>, val context: Context, val name : String, val txt :String, val url :String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_TITLE = 0
        const val TYPE_SELLER = 1
    }

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
                LayoutInflater.from(context).inflate(R.layout.artist_index_header, holder, false)
            )
            TYPE_SELLER -> SellerHolder(
                LayoutInflater.from(context).inflate(R.layout.artist_index_item, holder, false)
            )
            else -> TitleHolder(
                LayoutInflater.from(context).inflate(R.layout.artist_index_header, holder, false)
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


        var artist_name: TextView
        var artist_txt: TextView
        var top_flot: ImageView
        var back: ImageView

        init {

            artist_name = itemView.findViewById(R.id.artist_name)
            artist_txt = itemView.findViewById(R.id.artist_txt)
            top_flot = itemView.findViewById(R.id.top_flot)
            back = itemView.findViewById(R.id.back)

        }

        @SuppressLint("CheckResult")
        fun bindData() {
            artist_name.text = name
            artist_txt.text = txt
            Glide.with(context).load(url).placeholder(R.color.main_black_grey).into(back)

            RxView.clicks(top_flot)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    Observable.just(true).subscribe(ArtistDetActivity.observers)
                }

        }

    }

    inner class SellerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_cover: ImageView
        var title: TextView
        var txt: TextView

        init {
            iv_cover = itemView.findViewById(R.id.iv_cover)
            title = itemView.findViewById(R.id.title)
            txt = itemView.findViewById(R.id.txt)
        }

        fun bindData(position: Int) {
            Glide.with(context).load(datas[position].pic_url).placeholder(R.color.main_black_grey)
                .into(iv_cover)
            title.text = datas[position].name
            txt.text =  datas[position].info
        }
    }
}