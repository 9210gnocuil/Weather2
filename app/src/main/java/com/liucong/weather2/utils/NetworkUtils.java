package com.liucong.weather2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.liucong.weather2.other.MyApplication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liucong on 2017/3/9.
 * 网络相关的工具类
 */

public class NetworkUtils {

    /**
     * 判断网络是否可用
     *
     * @return :1.手机网络可用；2.wifi可用；-1不可用
     */
    public static int isNetworkAvailable(){

        int resultCode = -1;
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null){
            //飞行模式
            return -1;
        }
        if(networkInfo.isAvailable()){
            //网络可用，不一定在连接
            //判断连接方式
            NetworkInfo.State gprs = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            //如果手机网络为已连接则 返回true
            if(NetworkInfo.State.CONNECTED.equals(gprs) || NetworkInfo.State.CONNECTING.equals(gprs)){
                resultCode = 1;
            }
            if(NetworkInfo.State.CONNECTED.equals(wifi) || NetworkInfo.State.CONNECTING.equals(wifi)){
                resultCode = 2;
            }
        }
        return resultCode;
    }

    public static void get(String url,Callback callback){

        if(TextUtils.isEmpty(url)) {
            return ;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);

    }
}
