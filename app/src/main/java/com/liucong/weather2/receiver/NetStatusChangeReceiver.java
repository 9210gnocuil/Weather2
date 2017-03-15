package com.liucong.weather2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;

import com.liucong.weather2.activity.Main2Activity;
import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.service.UpdateService;
import com.liucong.weather2.utils.NetworkUtils;
import com.liucong.weather2.utils.ServiceUtils;
import com.liucong.weather2.utils.SharedPrefsUtil;

public class NetStatusChangeReceiver extends BroadcastReceiver {

    public NetStatusChangeReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //android.net.conn.CONNECTIVITY_CHANGE
        //当接收到这个消息的时候说明了手机网络扎un柜台发生变化了
        //则这时候需要检查网络是否连接，已连接则开始启动更新服务

        boolean isRunning = SharedPrefsUtil.get(ActivityConstant.UPDATE_SERVICE_IS_RUNNING, false);

        Intent service = new Intent(context,UpdateService.class);

        //先检查更新服务是否已经开启
        boolean serviceRunning = ServiceUtils.isServiceRunning(UpdateService.class.getCanonicalName());

        //网络可用 并且服务是允许开启的 并且当前服务没有开启
        if(NetworkUtils.isNetworkAvailable()!=-1 && isRunning &&!serviceRunning){
            //网络已连接
            //启动服务
            //首先检查服务是否正在运行，没运行启动
            context.startService(service);
        }

        //网络不可用 服务允许开启 服务正在运行
        //这时候需要停止服务 以免浪费电
        if(NetworkUtils.isNetworkAvailable()==-1 && isRunning && serviceRunning){
            //没有网络 没停掉就停掉
            context.stopService(service);
        }
    }
}
