package com.liucong.weather2.showweather;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.liucong.weather2.data.CityDataReponsitory;
import com.liucong.weather2.data.WeahterDataRepository;
import com.liucong.weather2.bean.WeatherResp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liucong-w on 2017/4/10.
 */

public class ShowWeatherPresenter implements ShowWeatherContract.Presenter {



    private static final String TAG = "ShowWeatherPresenter";


    private static Handler sHandler = new Handler(Looper.getMainLooper());
    /**
     * View
     */
    private ShowWeatherContract.View mView;
    /**
     * 天气数据仓库
     */
    private WeahterDataRepository mRepository = new WeahterDataRepository();

    public ShowWeatherPresenter(ShowWeatherContract.View mView) {
        this.mView = mView;
    }

    /**
     * 加载天气信息
     * @param forceUpdate
     */
    @Override
    public void loadWeather(final String city, final boolean forceUpdate) {
        mRepository.getWeatherInfo(city, forceUpdate,new WeahterDataRepository.WeatherListener() {
            @Override
            public void onSuccess(final WeatherResp info) {
                SystemClock.sleep(1000);
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView!=null) {
                            mView.showWeather(info.HeWeather5.get(0));
                            mView.hideLoading();
                        }
                    }
                });
            }
            @Override
            public void onFailure(Throwable e) {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView!=null) {
                            mView.showWeatherError();
                            mView.hideLoading();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void update(String city) {
        if(TextUtils.isEmpty(city)){
            city = CityDataReponsitory.getDefaultCity();
            if(TextUtils.isEmpty(city)){
                city = "beijing";
            }
        }
        loadWeather(city,true);
    }

    /**
     * Activity回调过来
     * @param requestCode 请求码
     * @param resultCode 结果码
     */
    @Override
    public void onResult(int requestCode, int resultCode,Intent intent) {
        //选择城市信息
        if(requestCode == 1023&&resultCode == 1024){
            String city = intent.getStringExtra("ChoosedCityName");
            if(!TextUtils.isEmpty(city)){
                //将这个城市设为默认城市
                CityDataReponsitory.setDefaultCity(city);
                mView.showSwipeRefreshLayout();
                update(city);
            }
        }
    }

    /**
     * 与mView断开关联
     */
    @Override
    public void onDetach() {
        mView = null;
    }

    @Override
    public void start() {
        mView.showLoading();
        mView.init();
        loadWeather("beijing",false);
    }

    /**
     * 初始化讯飞语
     * @param mTts
     * @param mWeahterinfo
     * @return
     */
    @Override
    public String initSpeaker(SpeechSynthesizer mTts,WeatherResp.WeatherInfo mWeahterinfo){
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        return formatingVoiceStr(mWeahterinfo);
    }

    /**
     * 格式化语音播报数据
     * @param mWeahterinfo
     * @return
     */
    private String formatingVoiceStr(WeatherResp.WeatherInfo mWeahterinfo){
        if(mWeahterinfo == null){
            return null;
        }
        WeatherResp.DailyForecast dailyForecast = mWeahterinfo.daily_forecast.get(0);

        String weatherStr = "今天是"+formatDate()+
                ",为你播报"+mWeahterinfo.basic.city+"今日天气：今天白天"+dailyForecast.cond.txt_d+
                ","+"夜间"+dailyForecast.cond.txt_n+
                ","+dailyForecast.tmp.min+"到"+dailyForecast.tmp.max+"度，"+
                dailyForecast.wind.sc;

        return weatherStr;
    }

    /**
     * 格式化日期
     * @return
     */
    private String formatDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return simpleDateFormat.format(new Date());
    }
}
