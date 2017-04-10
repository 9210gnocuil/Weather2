package com.liucong.weather2.choosecity;

import com.liucong.weather2.BasePresenter;
import com.liucong.weather2.BaseView;

import java.util.List;

/**
 * Created by liucong-w on 2017/4/10.
 */

public interface ChooseCityContract {

    interface View extends BaseView<ChooseCityContract.Presenter>{
        void init();
        void showLoading();
        void updateCityList(List<String> citys);
        void hideLoading();
        void returnPrevious(String result);
    }

    interface Presenter extends BasePresenter{
        void onItemClick(String city,int index);
    }
}
