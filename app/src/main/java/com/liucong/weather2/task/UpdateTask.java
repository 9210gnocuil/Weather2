package com.liucong.weather2.task;

import android.os.AsyncTask;

import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.utils.FileUtils;
import com.liucong.weather2.utils.InitDataUtils;
import com.liucong.weather2.utils.NetworkUtils;

/**
 * Created by liucong on 2017/3/11.
 */

public class UpdateTask extends AsyncTask<Void,Void,Integer> {

    private String cacheWeatherInfo;



    //结束后调用


    //子线程中调用 结束后返回结果到onPostExecute
    @Override
    protected Integer doInBackground(Void... params) {

        Update(new UpdateTaskResultListener() {
            @Override
            public void success(String result) {

            }

            @Override
            public void failure() {

            }
        });

        return 0;
    }

    private void Update(final UpdateTaskResultListener listener){
        InitDataUtils.initWeatherInfo(new InitDataUtils.ResponseListener() {
            @Override
            public void handResponse(String result, int type) {
                if (type == ActivityConstant.TYPE_WEATHER_INFO) {
                    //天气信息请求成功
                    //保存到本地cache文件中
                    FileUtils.saveWeatherInfoToCache(result);
                    //通知Activity更新UI。
                    listener.success(result);
                }
                if (type == ActivityConstant.TYPE_WEATHER_INFO_ERR) {
                    //天气信息请求失败
                    listener.failure();
                }
            }
        });

    }

    public interface UpdateTaskResultListener{
        void success(String result);
        void failure();
    }
}
