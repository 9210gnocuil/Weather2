package com.liucong.weather2.choosecity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.liucong.weather2.R;
import com.liucong.weather2.showweather.ShowWeatherActivity;

import java.util.List;

public class ChooseCityActivity extends AppCompatActivity implements ChooseCityContract.View{

    private ListView mChoosecityLv;
    private List<String> mCityList;
    private EnableUpdateAdapter mAdapter;
    private ChooseCityContract.Presenter mPresenter;
    private RelativeLayout mChooseCityLoading;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosecity);

        mPresenter = new ChooseCityPresenter(this);
        mPresenter.start();
    }

    @Override
    public void init() {
        mChoosecityLv = (ListView) findViewById(R.id.choosecity_lv);
        mChoosecityLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                count++;
                mPresenter.onItemClick(mAdapter.getItem(position),count);
            }
        });
        mChooseCityLoading = (RelativeLayout) findViewById(R.id.choosecity_rl_loading);
    }

    @Override
    public void showLoading() {
        if(mChooseCityLoading.getVisibility() != View.VISIBLE){
            mChooseCityLoading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateCityList(List<String> citys) {
        if(mAdapter == null){
            mAdapter = new EnableUpdateAdapter(ChooseCityActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,citys);
            mChoosecityLv.setAdapter(mAdapter);
        }else{
            mAdapter.setData(citys);
        }
    }

    @Override
    public void hideLoading() {
        if(mChooseCityLoading.getVisibility() != View.GONE){
            mChooseCityLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void returnPrevious(String result) {
        Intent intent = new Intent();
        intent.putExtra("ChoosedCityName",result);
        setResult(1024,intent);
        finish();
    }

    @Override
    public void setPresenter(ChooseCityContract.Presenter presenter) {

    }

    private class EnableUpdateAdapter extends ArrayAdapter<String>{

        private List<String> citys;

        public EnableUpdateAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
            citys = objects;
        }

        public void setData(List<String> citys){
            this.citys.clear();
            this.citys.addAll(citys);
            notifyDataSetChanged();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return citys.get(position);
        }
    }
}
