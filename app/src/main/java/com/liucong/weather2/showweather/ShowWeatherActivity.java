package com.liucong.weather2.showweather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.liucong.weather2.R;
import com.liucong.weather2.choosecity.ChooseCityActivity;
import com.liucong.weather2.data.CityDataReponsitory;
import com.liucong.weather2.bean.WeatherResp;
import com.liucong.weather2.data.WeahterDataRepository;
import com.liucong.weather2.other.MyApplication;
import com.squareup.picasso.Picasso;

import net.qiujuer.genius.blur.StackBlur;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liucong-w on 2017/4/10.
 */

public class ShowWeatherActivity extends AppCompatActivity implements View.OnClickListener,ShowWeatherContract.View {

    private static final int REQUEST_CODE_SELECT_LOC = 123;

    /**
     * 显示背景
     */
    @BindView(R.id.showweather_iv_bg)
    ImageView showIvBg;

    @BindView(R.id.showweather_srl_flush)
    SwipeRefreshLayout showSrl;

    @BindView(R.id.showweather_sv_root)
    ScrollView showSV;

    /**
     * 语音播报
     */
    @BindView(R.id.showweather_rl_speak)
    RelativeLayout showRlSpeak;


    /**
     * 显示语音播报天气文字提示
     */
    @BindView(R.id.showweather_tv_voice)
    TextView showTvVoice;

    /**
     * 显示更新时间
     */
    @BindView(R.id.showweather_tv_updateTime)
    TextView showTvUpdateTime;

    /**
     * 显示天气图片信息
     */
    @BindView(R.id.showweather_iv_weatherIcon)
    ImageView showIvWeaIcon;

    /**
     * 显示天气文字信息
     */
    @BindView(R.id.showweather_tv_weatherInfo)
    TextView showTvWeaInfo;

    /**
     * 显示当前地点
     */
    @BindView(R.id.showweather_tv_location)
    TextView showTvLoc;

    @BindView(R.id.showweather_ll_location)
    LinearLayout showLlLoc;


    @BindView(R.id.showweather_rl_loading)
    RelativeLayout showRlLoading;


    private WeatherResp.WeatherInfo mWeahterinfo;
    private ShowWeatherContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_showweather);

        Log.i("ShowWeatherActivity", "onCreate: ");
        ButterKnife.bind(this);
        mPresenter = new ShowWeatherPresenter(this);
        mPresenter.start();
    }


    //刷新界面
    private void refreshUI(WeatherResp.WeatherInfo info){

        // 中间显示天气状况图片：main2_iv_weatherIcon
        Picasso.with(MyApplication.getContext()).load(WeahterDataRepository.URL_WEATHER_ICON+info.now.cond.code+".png")
                .into(showIvWeaIcon);
        // 天气状况 文字 main2_tv_weatherInfo
        showTvWeaInfo.setText(info.now.cond.txt);

        //更新时间 main2_tv_updateTime
        showTvUpdateTime.setText("更新:"+info.basic.update.loc);

        //输入位置信息
        showTvLoc.setText(info.basic.city);
    }

    private void initView() {
        //高斯模糊
        Bitmap bitmapOri = BitmapFactory.decodeResource(getResources(), R.drawable.static_02);
        Bitmap newBitmap = StackBlur.blurNatively(bitmapOri, (int) 10, false);
        //将模糊后的bitmap设置为背景
        showIvBg.setImageBitmap(newBitmap);

        showTvLoc.setOnClickListener(this);
        showTvVoice.setOnClickListener(this);
        showSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.update(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.showweather_tv_voice:

                SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(ShowWeatherActivity.this, null);
                String str = mPresenter.initSpeaker(mTts, mWeahterinfo);
                mTts.startSpeaking(str, mSynListener);
                break;
            case R.id.showweather_tv_location:
                Intent intent = new Intent(ShowWeatherActivity.this, ChooseCityActivity.class);
                startActivityForResult(intent,1023);
                break;
            default:
                Log.i("onClick", "onClick:s ");
                break;
        }
    }



    /**
     * 讯飞语音合成所需要的
     */
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            //将按钮禁用
            showRlSpeak.setEnabled(false);
            showTvVoice.setText("正在播报天气...");
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            //将按钮启用
            showRlSpeak.setEnabled(true);
            showTvVoice.setText("点击播报天气");
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
    };

    @Override
    public void init() {
        initView();
        CityDataReponsitory.copyDb("city.db");
    }

    /**
     * 显示加载
     */
    @Override
    public void showLoading() {
        showRlLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSwipeRefreshLayout() {
        if(!showSrl.isRefreshing()){
            showSrl.setRefreshing(true);
        }
    }

    /**
     * 获取天气过程中出错了
     */
    @Override
    public void showWeatherError() {
        Toast.makeText(ShowWeatherActivity.this,"哎呀出问题了",Toast.LENGTH_SHORT).show();
    }

    /**
     * 展示天气信息
     */
    @Override
    public void showWeather(WeatherResp.WeatherInfo weatherInfo) {
        mWeahterinfo = weatherInfo;
        refreshUI(weatherInfo);
    }

    /**
     * 隐藏加载
     */
    @Override
    public void hideLoading() {
        if(showRlLoading.getVisibility()==View.VISIBLE){
            showRlLoading.setVisibility(View.GONE);
        }

        if(showSrl.isRefreshing()){
            Toast.makeText(ShowWeatherActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            showSrl.setRefreshing(false);
        }
    }

    /**
     * 绑定Presenter
     * @param presenter
     */
    @Override
    public void setPresenter(ShowWeatherContract.Presenter presenter) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }
}
