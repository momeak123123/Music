package com.example.xiaobai.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.xuexiang.xui.utils.ResUtils
import io.reactivex.Observable

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

        holder.more.setOnClickListener {
            if(datas.size>1){
                if(MusicPlayActivity.song_id == datas[position].song_id){

                    if (MusicPlayActivity.bool) {
                        notifyItemRemoved(position)
                        if (position != datas.size) {
                            notifyItemRangeChanged(position, datas.size - position)
                        }
                        MusicApp.getMusic().removeAt(position)
                        Observable.just(2).subscribe(MusicPlayActivity.observerset)
                    } else {
                        Toast.makeText(
                            context,
                            context.getText(R.string.secret_num),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{
                    notifyItemRemoved(position)
                    if (position != datas.size) {
                        notifyItemRangeChanged(position, datas.size - position)
                    }
                    MusicApp.getMusic().removeAt(position)

                }
            }else{
                Observable.just(true).subscribe(MusicPlayActivity.observer)
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