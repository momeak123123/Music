package com.example.music.xiaobai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.music.xiaobai.R
import com.example.music.xiaobai.bean.Music
import com.example.music.xiaobai.music.view.act.MusicPlayActivity
import com.xuexiang.xui.utils.ResUtils

class PlayListAdapter  (val datas: MutableList<Music>?, val context: Context) : RecyclerView.Adapter<PlayListAdapter.InnerHolder>() {


    private var mItemClickListener: ItemClickListener? = null


    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.play_playlist_item, holder, false)

        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas!!.size

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
        holder.itemView.setOnClickListener { v ->
            mItemClickListener?.onItemClick(v,position)
        }
        Glide.with(context).load(R.drawable.del_black).into(holder.more)
        holder.title.text = datas?.get(position)!!.name
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

        if(MusicPlayActivity.id==position){
            println(""+MusicPlayActivity.id+"/"+position)
            holder.txt.setTextColor(ResUtils.getResources().getColor(R.color.lightBlue))
            holder.title.setTextColor(ResUtils.getResources().getColor(R.color.lightBlue))
        }else{
            holder.txt.setTextColor(ResUtils.getResources().getColor(R.color.gray))
            holder.title.setTextColor(ResUtils.getResources().getColor(R.color.black))
        }

    }
    interface ItemClickListener {
        fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }

}