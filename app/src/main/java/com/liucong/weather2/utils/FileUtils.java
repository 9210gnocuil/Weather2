package com.liucong.weather2.utils;


import android.content.Context;
import android.util.Log;

import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.other.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liucong on 2017/3/10.
 */

public class FileUtils {

    public static final String TAG = "FileUtils";

    //检查数据库是否存在
    public static boolean checkDatabase() {

        File file = new File(ActivityConstant.DATABASE_LOC_CITY);
        boolean exists = file.exists();
        if(exists){
            Log.i(TAG,"checkCache"+"数据库数据存在");
        }else{
            Log.i(TAG,"checkCache"+"数据库数据不存在");

        }
        return exists;
    }

    //检查缓存天气文件是否存在
    public static boolean checkCache() {

        File file = new File(ActivityConstant.CACHE_WEATHER_LOC);
        boolean exists = file.exists();
        if(exists){
            Log.i(TAG,"checkCache"+"天气缓存数据存在");
        }else{
            Log.i(TAG,"checkCache"+"天气缓存数据不存在");

        }
        return exists;
    }

    // 从缓存中读取
    public static String readCacheWeatherInfo(){
        String result = null;
        BufferedInputStream bis = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            fis = MyApplication.getContext().openFileInput(ActivityConstant.CACHE_WEATHER_FILENAME_IN_DES);
            bis = new BufferedInputStream(fis);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = bis.read(buf)) != -1){
                baos.write(buf,0,len);
                baos.flush();
            }
            result = baos.toString();
            Log.i(TAG, "缓存文件读取成功");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG,"缓存文件不存在");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"缓存文件读取失败");
        } finally {
            StreamUtils.closeStream(baos);
            StreamUtils.closeStream(bis);
            StreamUtils.closeStream(fis);
        }

        return result;
    }

    //将请求的数据保存到用户目录下面
    public static void saveWeatherInfoToCache(String result) {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bos = null;
        try {
            fileOutputStream = MyApplication.getContext().openFileOutput(ActivityConstant.CACHE_WEATHER_FILENAME_IN_DES, Context.MODE_PRIVATE);
            bos =  new BufferedOutputStream(fileOutputStream);
            byte[] bytes = result.getBytes();
            bos.write(bytes,0,bytes.length);
            bos.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeStream(fileOutputStream);
            StreamUtils.closeStream(bos);
        }
    }

}
