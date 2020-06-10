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

class PlayListAdapter  (val datas: MutableList<Music>, val context: Context) : RecyclerView.Adapter<PlayListAdapter.InnerHolder>() {

    private var itemClickListener: IKotlinItemClickListener? = null

    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.play_playlist_item, holder, false)
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
        var title: TextView = itemView.findViewById(R.id.title)
        var txt: TextView = itemView.findViewById(R.id.txt)
        var more: ImageView = itemView.findViewById(R.id.more)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.title.text = datas[position].name
        val artist =  datas[position].all_artist
        var srtist_name = ""
        for(it in artist){
            if(srtist_name != ""){
                srtist_name += "/"+it.name
            }else{
                srtist_name = it.name
            }

        }
        holder.txt.text =  srtist_name

    }

    // 提供set方法
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface IKotlinItemClickListener {
        fun onItemClickListener(position: Int)
    }
}