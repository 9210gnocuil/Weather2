package com.liucong.weather2.showweather;

import android.content.Intent;

import com.iflytek.cloud.SpeechSynthesizer;
import com.liucong.weather2.BasePresenter;
import com.liucong.weather2.BaseView;
import com.liucong.weather2.bean.WeatherResp;

/**
 * Created by liucong-w on 2017/4/10.
 */

public interface ShowWeatherContract {
    /**
     */
    interface View extends BaseView<Presenter>{

        void init();
        void showLoading();
        void showSwipeRefreshLayout();
        void showWeatherError();
        void showWeather(WeatherResp.WeatherInfo weatherInfo);
        void hideLoading();
    }

    interface Presenter extends BasePresenter{
        String initSpeaker(SpeechSynthesizer mTts, WeatherResp.WeatherInfo mWeahterinfo);
        void loadWeather(String city,boolean forceUpdate);
        void update(String city);
        void onResult(int requestCode,int resultCode,Intent intent);
        void onDetach();
    }
}
