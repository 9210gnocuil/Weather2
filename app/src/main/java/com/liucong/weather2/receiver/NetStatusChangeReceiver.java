package com.liucong.weather2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;

import com.liucong.weather2.utils.NetworkUtils;

public class NetStatusChangeReceiver extends BroadcastReceiver {

    public NetStatusChangeReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //android.net.conn.CONNECTIVITY_CHANGE
        //当接收到这个消息的时候说明了手机网络扎un柜台发生变化了
        //则这时候需要检查网络是否连接，已连接则开始启动更新服务
        if(NetworkUtils.isNetworkAvailable()!=0){
            //网络已连接
            //启动服务

        }
    }
}
