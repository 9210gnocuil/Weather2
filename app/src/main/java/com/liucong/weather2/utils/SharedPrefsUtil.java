package com.liucong.weather2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.liucong.weather2.other.MyApplication;

/**
 * Created by liucong on 2017/3/10.
 */

public class SharedPrefsUtil {

    static SharedPreferences sp;
    private static SharedPreferences getSharedPrefs(){
        if(sp == null){
            sp = MyApplication.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static void save(String key, int value) {

        getSharedPrefs().edit().putInt(key,(int)value).commit();
    }
    public static void save(String key, String value) {

        getSharedPrefs().edit().putString(key,value).commit();
    }
    public static void save(String key, boolean value) {

        getSharedPrefs().edit().putBoolean(key,value).commit();
    }

    public static int get(String key,int defaultValue) {
        return getSharedPrefs().getInt(key,defaultValue);
    }

    public static boolean get(String key,boolean defaultValue) {
        return getSharedPrefs().getBoolean(key,defaultValue);
    }

    public static String get(String key,String defaultValue) {
        return getSharedPrefs().getString(key,defaultValue);
    }
}
