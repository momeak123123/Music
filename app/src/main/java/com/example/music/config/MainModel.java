package com.example.music.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.music.bean.ResultBean;
import com.example.music.bean.ResultBeans;
import com.example.music.common.Constants;
import com.example.music.music.view.fragment.FindFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class MainModel {

    public static void adddata(Context context,String txt){
        SharedPreferences sp;
        sp = context.getSharedPreferences("User", Context.MODE_PRIVATE);

        OkGo.<String>post(Constants.URL + "api/user/create_play_list")
                .params("token", sp.getString("token", ""))
                .params("name", txt)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {

                        Gson gson = new Gson();
                        ResultBean bean = gson.fromJson(response.body(), ResultBean.class);

                        if(bean.getCode()==200){
                            Observable.just(bean.getData()).subscribe(FindFragment.observer);
                        } else {
                            Toast.makeText(context, bean.getMsg() + "", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
