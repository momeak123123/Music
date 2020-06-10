package com.example.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.bean.Artists
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Observer

class ArtistListAdapter (var datas:MutableList<Artists>, val context: Context) : RecyclerView.Adapter<ArtistListAdapter.InnerHolder>() {

    private var itemClickListener: IKotlinItemClickListener? = null



    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.artist_list_item, holder, false)
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

    }

    /**
     * 绑定数据，View和数据绑定
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        Glide.with(context).load(datas[position].pic_url).placeholder(R.color.main_black_grey).into(holder.iv_cover)
        holder.title.text = datas[position].name
    }

    // 提供set方法
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface IKotlinItemClickListener {
        fun onItemClickListener(position: Int)
    }

    fun removeAll(){
        datas.removeAll(datas)

    }

    fun addAll(data: MutableList<Artists>){
        datas.addAll(data)
    }

}