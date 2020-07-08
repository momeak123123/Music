package com.example.xiaobai.music.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.xiaobai.music.LockActivity;

import java.util.Objects;

public class LockService extends Service {
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF) || Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)) {
                    Intent lockscreen = new Intent(LockService.this, LockActivity.class);
                    lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockscreen);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
