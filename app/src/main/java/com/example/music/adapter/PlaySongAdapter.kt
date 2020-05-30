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
import com.example.music.bean.Song

class PlaySongAdapter (val datas: List<Song>, val context: Context) : RecyclerView.Adapter<PlaySongAdapter.InnerHolder>() {

    private var itemClickListener: IKotlinItemClickListener? = null

    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.play_songlist_item, holder, false)
        itemView.setOnClickListener {
            itemClickListener?.onItemClickListener(position)
        }
        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas.size

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_cover: ImageView = itemView.findViewById(R.id.iv_cover)
        var title: TextView = itemView.findViewById(R.id.title)
        var txt: TextView = itemView.findViewById(R.id.txt)
        var right: ImageView = itemView.findViewById(R.id.right)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val albummap = datas[position].album
        val artists = datas[position].artists
        Glide.with(context).load(albummap["album_picurl"]).placeholder(R.drawable.gplugin_load)
            .into(holder.iv_cover)
        holder.title.text = datas[position].song_name
        for (it in artists) {
            if (holder.txt.text == "") {
                holder.txt.text = it.artist_name
            } else {
                holder.txt.text = holder.txt.text.toString() + "/" + it.artist_name
            }

        }

    }

    // 提供set方法
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface IKotlinItemClickListener {
        fun onItemClickListener(position: Int)
    }
}