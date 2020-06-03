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
import com.example.music.bean.Song
import com.example.music.bean.SongDet

class SongDetAdapter  (val datas: MutableList<Music>, val context: Context,val imaurl:String) : RecyclerView.Adapter<SongDetAdapter.InnerHolder>() {

    private var itemClickListener: IKotlinItemClickListener? = null

    var type = 0

    var listdet = mutableListOf<SongDet>()
    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.song_index_item, holder, false)
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
        var iv_cover: ImageView = itemView.findViewById(R.id.musicsrc)
        var title: TextView = itemView.findViewById(R.id.musicname)
        var txt: TextView = itemView.findViewById(R.id.singer)
        var more: ImageView = itemView.findViewById(R.id.more)
        var radio: ImageView = itemView.findViewById(R.id.radio)
        var num: TextView = itemView.findViewById(R.id.num)
        var delete: TextView = itemView.findViewById(R.id.delete)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        Glide.with(context).load(imaurl).placeholder(R.color.main_black_grey).into(holder.iv_cover)
        holder.title.text = datas[position].title
        holder.txt.text =  datas[position].author


        if(type==0){
            holder.num.visibility = View.VISIBLE
            holder.radio.visibility = View.GONE
            holder.num.text = (position+1).toString()
        }else{
            holder.num.visibility = View.GONE
            holder.radio.visibility = View.VISIBLE
            for(it in datas){
                listdet.add(SongDet(it,0))
            }
        }

        holder.radio.setOnClickListener {
            if(listdet[position].type==0){
                listdet[position].type = 1
                Glide.with(context).load(R.drawable.select).into(holder.radio)
            }else{
                listdet[position].type = 0
                Glide.with(context).load(R.drawable.upselect).into(holder.radio)
            }

        }

        holder.more.setOnClickListener {

        }

        holder.delete.setOnClickListener {
            remove(holder.adapterPosition)
        }
    }

    // 提供set方法
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface IKotlinItemClickListener {
        fun onItemClickListener(position: Int)
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