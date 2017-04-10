package com.liucong.weather2.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.liucong.weather2.bean.WeatherResp;
import com.liucong.weather2.other.MyApplication;
import com.liucong.weather2.utils.HttpUtils;
import com.liucong.weather2.utils.StreamUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liucong-w on 2017/4/10.
 * 提供数据
 */

public class WeahterDataRepository{

    public static final String TAG = "WeahterDataRepository";

    /**
     * 天气缓存文件名
     */
    public static final String CACHE_FILE_NAME = "weatherCache.txt";
    /**
     * 请求天气时使用的api密钥
     */
    public static final String APIKEY = "&key=e0ab447921874bf6af6ee786b0997baa";
    /**
     * 获取城市信息列表的网络地址
     */
    public static final String URL_CITY_LIST = "http://files.heweather.com/china-city-list.json";
    /**
     * 获取天气信息的网络地址
     */
    public static final String URL_WEATHER_INFO = "https://free-api.heweather.com/v5/weather?";
    /**
     * 获取天气图片的网络地址
     */
    public static final String URL_WEATHER_ICON = "http://files.heweather.com/cond_icon/";

    /**
     * 获取天气时回调天气数据接口
     */
    public interface WeatherListener{
        void onSuccess(WeatherResp info);
        void onFailure(Throwable e);
    }

    /**
     * 首先从网络中读取
     */

    public void getWeatherInfo(final String city, final boolean needUpdate , final WeatherListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String weatherInfo = null;
                if(!needUpdate){
                    weatherInfo = readWeahterInfo();
                }

                //缓存中没有 还是需要从网络上请求数据
                if(TextUtils.isEmpty(weatherInfo)){
                    try {
                        weatherInfo= HttpUtils.request(URL_WEATHER_INFO + "city=" + city + APIKEY);
                        //将天气信息缓存起来
                        if(!TextUtils.isEmpty(weatherInfo)){saveWeatherInfo(weatherInfo);}
                    } catch (IOException e) {
                        listener.onFailure(e);
                    }
                }

                if(!TextUtils.isEmpty(weatherInfo)){
                    WeatherResp resp = new Gson().fromJson(weatherInfo, WeatherResp.class);
                    listener.onSuccess(resp);
                }else{
                    listener.onFailure(new Exception("解析失败"));
                }
            }
        }).start();
    }

    /**
     * 从本地缓存中读取天气信息
     * @return
     */
    public String readWeahterInfo(){
        String result = null;
        BufferedInputStream bis = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            fis = MyApplication.getContext().openFileInput(CACHE_FILE_NAME);
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

    /**
     * 将天气信息保存到本地缓存中
     * @param info
     */
    public void saveWeatherInfo(String info) {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bos = null;
        try {
            fileOutputStream = MyApplication.getContext().openFileOutput(CACHE_FILE_NAME, Context.MODE_PRIVATE);
            bos =  new BufferedOutputStream(fileOutputStream);
            byte[] bytes = info.getBytes();
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


    public void deleteWeatherInfo() {

    }
}
