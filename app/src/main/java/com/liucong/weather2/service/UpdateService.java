package com.liucong.weather2.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.utils.FileUtils;
import com.liucong.weather2.utils.InitDataUtils;
import com.liucong.weather2.utils.SharedPrefsUtil;

/**
 * 后台更新天气信息
 * 如何使用:
 * 在MainActivity中：如果需要设置自动更新的间隔事件 则可以这样来设置
 *      intent.putExtra("update",1000*3600);//设置每1小时自动更新
 *      startService(intent);
 *      默认自动更新事件为1小时
 */

public class UpdateService extends Service {

    private static final int UPDATESERVICE_CODE_RESULT_OK = 1;
    private static final int UPDATESERVICE_CODE_RESULT_ERR = 2;

    private Intent updateNowIntent;
    private Intent updateErrIntent;

    private long updateTimeInterval;

    private boolean eshCompleted = true;

    public UpdateService() {
        handler = new Handler();
        updateTimeInterval = SharedPrefsUtil.get(ActivityConstant.UPDATE_TIME_INTERVAL,1000*3600);
        updateNowIntent = new Intent("com.liucong.weather2.UPDATEUI.CACHE_OK");
        updateErrIntent = new Intent("com.liucong.weather2.UPDATEUI.CACHE_FAIL");
    }

    private static Handler handler;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        handler.removeCallbacks(updateRunnaleTest);
        handler = null;
        updateRunnaleTest = null;

        Log.i("UpdateService","自动更新服务已经关闭");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //拿到Activity中的数据
        long interval = intent.getLongExtra("TimeInterval", 0);
        if(interval!=0){
            // 说明不是第一次启动服务
            // 这是有数据了
            // 将更新时间间隔设置为Activity传送过来的事件
            Log.i("UpdateService","自动更新时间已经更改");
            updateTimeInterval = interval;
        }

        handler.postDelayed(updateRunnaleTest,updateTimeInterval);

        Log.i("UpdateService","自动更新时间为:"+updateTimeInterval/1000/3600+"小时");

        //开启子线程请求数据，保存到本地
        //每次启动这个方法就要将之前没有执行的runbale任务取消掉
        //handler.removeCallbacks(updateRunnale);
        //重新用设定的事件进行更新
        //handler.postDelayed(updateRunnale,updateTimeInterval);

        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable updateRunnaleTest = new Runnable(){
        @Override
        public void run() {
            //如果多次启动 则将前面的run给取消掉 保证不会多次执行
            handler.removeCallbacks(this);
            Log.i("onStartCommand","服务正在更新天气");
            Log.i("UpdateService", "更新数据中:"+ SystemClock.uptimeMillis());
            handler.postDelayed(this,updateTimeInterval);
        }
    };
    private Runnable updateRunnale = new Runnable() {
        @Override
        public void run() {
            //如果多次启动 则将前面的run给取消掉
            handler.removeCallbacks(this);
            InitDataUtils.initWeatherInfo(new InitDataUtils.ResponseListener() {

                @Override
                public void handResponse(String result, int type) {
                    if (type == ActivityConstant.TYPE_WEATHER_INFO) {

                        //天气信息请求成功
                        Log.i("服务", "Update: "+"数据请求成功");

                        //保存到本地cache文件中
                        FileUtils.saveWeatherInfoToCache(result);
                        Log.i("服务", "Update: "+"数据保存到本地成功");

                        //发送广播通知Activity更新UI。

                        sendBroadcast(updateNowIntent);
                        Log.i("服务", "Update: "+"数据发送到Handler成功");

                        //每隔1小时更新一次
                        handler.postDelayed(updateRunnale,updateTimeInterval);

                    }

                    if (type == ActivityConstant.TYPE_WEATHER_INFO_ERR) {
                        //天气信息请求失败
                        Log.i("服务", "Update: "+"天气信息请求失败");
                        sendBroadcast(updateNowIntent);
                    }

                    //失败了也要更新，不过需要先判断是否有网络
                    //每隔updateTimeInterval事件更新一次
                    handler.postDelayed(updateRunnale,updateTimeInterval);
                }
            });


        }
    };

    /**
     * 更新数据
     */
    private void Update(){
        InitDataUtils.initWeatherInfo(new InitDataUtils.ResponseListener() {

            @Override
            public void handResponse(String result, int type) {
                if (type == ActivityConstant.TYPE_WEATHER_INFO) {

                    //天气信息请求成功
                    Log.i("服务", "Update: "+"数据请求成功");

                    //保存到本地cache文件中
                    FileUtils.saveWeatherInfoToCache(result);
                    Log.i("服务", "Update: "+"数据保存到本地成功");

                    //发送广播通知Activity更新UI。

                    sendBroadcast(updateNowIntent);
                    Log.i("服务", "Update: "+"数据发送到Handler成功");

                }

                if (type == ActivityConstant.TYPE_WEATHER_INFO_ERR) {
                    //天气信息请求失败
                    Log.i("服务", "Update: "+"天气信息请求失败");
                    sendBroadcast(updateNowIntent);
                }
            }
        });

    }

}
