package com.liucong.weather2.utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.liucong.weather2.bean.Citys;
import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.constant.DBConstant;
import com.liucong.weather2.constant.NetworkConstant;
import com.liucong.weather2.other.MyApplication;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by liucong on 2017/3/10.
 */

public class InitDataUtils {

    public static final String TAG = "InitDataUtils";

    public interface ResponseListener{
        void handResponse(String result,int type);
    }

    public static void initWeatherInfo(ResponseListener listener){
        initWeatherInfo(null,listener);
    }

    public static void initWeatherInfo(String city,final ResponseListener listener) {

        //city=beijing，city=CN101010100，city= 60.194.130.1
        //这个city可以为城市名称可以为城市id可以为城市ip
        //因此可以使用 GPS定位、ip定位等方法拿到city数据

        if(TextUtils.isEmpty(city)){
            Toast.makeText(MyApplication.getContext(),"天气信息不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        //在这里请求天气信息数据
        String url = NetworkConstant.URL_GETWEATHERINFO+"city="+city+NetworkConstant.APIKEY;
        NetworkUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //天气信息请求失败
                if (listener != null) {
                    listener.handResponse("Weather_Error", ActivityConstant.TYPE_WEATHER_INFO_ERR);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();

                Log.i(TAG, "result:"+result);
                if (listener != null) {
                    listener.handResponse(result, ActivityConstant.TYPE_WEATHER_INFO);
                }
            }
        });
    }

    //初始化数据库，如果数据库不存在就创建。
    public static void initDatabase(final ResponseListener listener) {

        //首先检查数据库是否创建过
        boolean inited = SharedPrefsUtil.get(DBConstant.KEY_BD_INITED, false);

        if(inited){
            if (listener != null) {
                listener.handResponse("City_Inited", ActivityConstant.TYPE_CITY_INFO_ERR);
            }
            Log.i(TAG, "数据库已经创建过了 无需重复创建");
            return;
        }

        //请求数据并解析然后存放到数据库中
        NetworkUtils.get(NetworkConstant.URL_CITY_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "数据库添加数据失败");

                //城市信息请求失败就等待一段时间后进入主页面
                if (listener != null) {
                    listener.handResponse("City_Error", ActivityConstant.TYPE_CITY_INFO_ERR);
                }
            }

            //解析 存储
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if (listener != null) {
                    listener.handResponse(json, ActivityConstant.TYPE_CITY_INFO);
                }
            }
        });
    }
}
