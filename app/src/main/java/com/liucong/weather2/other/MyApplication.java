package com.liucong.weather2.other;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by liucong on 2017/3/9.
 * 创建一个Application类 用于提供全局Context
 */

public class MyApplication extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=58cb67f4");
    }

    public static Context getContext(){
        return context;
    }
}
