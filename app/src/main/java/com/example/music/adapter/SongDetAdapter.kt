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
import com.example.music.bean.Music
import com.example.music.bean.SongDet
import com.example.music.music.view.act.AlbumDetActivity
import com.example.music.music.view.act.SongDetActivity
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.head.*
import java.util.concurrent.TimeUnit

class SongDetAdapter  (val datas: MutableList<Music>, val context: Context,
                       val covers: String,
                       val names:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        const val TYPE_TITLE = 0
        const val TYPE_SELLER = 1
    }

    var type = 0
    private var mItemClickListener: SongDetAdapter.ItemClickListener? = null
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

        init {
            iv_cover = itemView.findViewById(R.id.iv_cover)
            txt = itemView.findViewById(R.id.txt)
            top_flot = itemView.findViewById(R.id.top_flot)
            title = itemView.findViewById(R.id.title)
            top_set = itemView.findViewById(R.id.top_set)
        }

        @SuppressLint("CheckResult")
        fun bindData() {
            Glide.with(context).load(covers).placeholder(R.color.main_black_grey).into(iv_cover)
            title.text = names
            Glide.with(context).load(R.drawable.mores).placeholder(R.color.main_black_grey).into(top_set)

            RxView.clicks(top_flot)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    Observable.just(true).subscribe(SongDetActivity.observers)
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
        var delete: ImageView

        init {
             iv_cover = itemView.findViewById(R.id.musicsrc)
             title = itemView.findViewById(R.id.musicname)
             txt = itemView.findViewById(R.id.singer)
             more = itemView.findViewById(R.id.more)
             radio = itemView.findViewById(R.id.radio)
             num = itemView.findViewById(R.id.num)
             delete = itemView.findViewById(R.id.delete)
        }

        fun bindData(position: Int) {
            itemView.setOnClickListener { v ->
                mItemClickListener?.onItemClick(v,position)
            }
            Glide.with(context).load(datas[position].pic_url).placeholder(R.color.main_black_grey).into(iv_cover)

            title.text = datas[position].name
            val artist =  datas[position].all_artist
            var srtist_name = ""
            for(it in artist){
                if(srtist_name != ""){
                    srtist_name += "/"+it.name
                }else{
                    srtist_name = it.name
                }

            }
            txt.text =  srtist_name


            if(type==0){
                num.visibility = View.VISIBLE
                radio.visibility = View.GONE
                num.text = (position+1).toString()
            }else{
                num.visibility = View.GONE
                radio.visibility = View.VISIBLE
                for(it in datas){
                    listdet.add(SongDet(it,0))
                }
            }

            radio.setOnClickListener {
                if(listdet[position].type==0){
                    listdet[position].type = 1
                    Glide.with(context).load(R.drawable.select).into(radio)
                }else{
                    listdet[position].type = 0
                    Glide.with(context).load(R.drawable.upselect).into(radio)
                }

            }


            more.setOnClickListener {

            }

            delete.setOnClickListener {
                remove(adapterPosition)
            }
        }
    }


    interface ItemClickListener {
        fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }


    fun add(item: Music) {
        datas.add(item)
        notifyItemInserted(datas.size)
    }

    fun remove(position: Int) {
        datas.removeAt(position)
        notifyItemRemoved(position)

    }
}