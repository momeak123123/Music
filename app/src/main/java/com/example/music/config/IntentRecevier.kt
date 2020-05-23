package com.example.music.config

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast

class IntentRecevier(): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network =manager.activeNetwork
        if(network!=null){
            val nc=manager.getNetworkCapabilities(network)
            if(nc!=null){
                if(nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){//WIFI
                    Toast.makeText(context, " wifi", Toast.LENGTH_SHORT).show()
                }else if(nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){//移动数据
                    Toast.makeText(context, "移动数据", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(context, "无网络连接", Toast.LENGTH_SHORT).show()

        }

    }
}