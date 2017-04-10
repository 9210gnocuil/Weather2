package com.liucong.weather2.choosecity;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.liucong.weather2.data.CityDataReponsitory;

import java.util.List;

/**
 * Created by liucong-w on 2017/4/10.
 */

public class ChooseCityPresenter implements ChooseCityContract.Presenter {

    private Handler handler = new Handler(Looper.getMainLooper());

    private ChooseCityContract.View mView;

    public ChooseCityPresenter(ChooseCityContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void start() {
        mView.init();
        mView.showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> provinces = CityDataReponsitory.queryAllProvince();
                SystemClock.sleep(1000);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.updateCityList(provinces);
                        mView.hideLoading();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(final String city, final int index) {
        Log.i("ChooseCityPresenter", "onItemClick:City="+city);
        if(index<3){
            mView.showLoading();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(city)){
                    final List<String> alls = CityDataReponsitory.queryAll(city);
                    Log.i("ChooseCityPresenter", "onItemClick:CityList="+alls);
                    if(alls!=null && index <= 2){
                        SystemClock.sleep(1000);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mView.updateCityList(alls);
                                mView.hideLoading();
                            }
                        });
                    }else{
                        //已经显示到县镇了 可以跳转到主页面中。
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mView.returnPrevious(city);
                                mView.hideLoading();
                            }
                        });

                    }
                }
            }
        }).start();

    }
}
