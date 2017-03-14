package com.liucong.weather2.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liucong.weather2.R;
import com.liucong.weather2.bean.Citys;
import com.liucong.weather2.bean.WeatherResp;
import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.constant.DBConstant;
import com.liucong.weather2.constant.NetworkConstant;
import com.liucong.weather2.other.MyApplication;
import com.liucong.weather2.service.UpdateService;
import com.liucong.weather2.utils.DBUtils;
import com.liucong.weather2.utils.FileUtils;
import com.liucong.weather2.utils.InitDataUtils;
import com.liucong.weather2.utils.JsonParseUtils;
import com.liucong.weather2.utils.NetworkUtils;
import com.liucong.weather2.utils.SharedPrefsUtil;
import com.liucong.weather2.view.HistogramView;
import com.squareup.picasso.Picasso;

import net.qiujuer.genius.blur.StackBlur;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "Main2Activity";

    private static final int UPDATE_UI = 666;
    private static final int UPDATE_UI_ERR = 669;
    private static final int EXIT_APP_DELAY = 667;

    private static final int REQUEST_CODE_SELECT_LOC = 123;


    @BindView(R.id.main2_content_root)
    CoordinatorLayout main2_content_root;

    @BindView(R.id.main2_sug_cv_root)
    CardView main2SugRoot;

    @BindView(R.id.main2_hv_temp)
    HistogramView main2HvTemp;
    @BindView(R.id.main2_iv_bg)
    ImageView main2IvBg;


    // 中间显示天气状况图片：main2_iv_weatherIcon
    @BindView(R.id.main2_iv_weatherIcon)
    ImageView main2IvWeaIcon;

    // 天气状况 文字 main2_tv_weatherInfo
    @BindView(R.id.main2_tv_weatherInfo)
    TextView main2TvWeaChs;

    // aqi main_tv_aqi main2_cv_aqi
    @BindView(R.id.main_tv_aqi)
    TextView main2Tvaqi;
    @BindView(R.id.main2_cv_aqi)
    CardView main2Cvaqi;

    //预警 main_tv_warn main2_cv_warn
    @BindView(R.id.main_tv_warn)
    TextView main2TvWarm;
    @BindView(R.id.main2_cv_warn)
    CardView main2CvWarm;

    //更新时间 main2_tv_updateTime
    @BindView(R.id.main2_tv_updateTime)
    TextView main2TvUpdateTime;

    //main2_ll_location
    @BindView(R.id.main2_ll_location)
    LinearLayout main2LlLoc;

    //位置:main2_tv_location
    @BindView(R.id.main2_tv_location)
    TextView main2TvLoc;

    //湿度 main2_tv_humidity
    @BindView(R.id.main2_tv_humidity)
    TextView main2TvHumidity;

    //能见度 main2_tv_visibility
    @BindView(R.id.main2_tv_visibility)
    TextView main2TvVisible;

    //日落 main2_tv_sunset
    @BindView(R.id.main2_tv_sunset)
    TextView main2TvSunset;
    //日出 main2_tv_sunrise
    @BindView(R.id.main2_tv_sunrise)
    TextView main2TvSunrise;


    @BindView(R.id.main2_rl_loading)
    RelativeLayout main2RlLoading;

    //几个生活指数
    @BindView(R.id.main2_sug_shushidu_res)
    TextView main2SugSSD;
    @BindView(R.id.main2_sug_xiche_res)
    TextView main2SugXC;
    @BindView(R.id.main2_sug_chuanyi_res)
    TextView main2SugCY;
    @BindView(R.id.main2_sug_ganmao_res)
    TextView main2SugGM;
    @BindView(R.id.main2_sug_yundong_res)
    TextView main2SugYD;
    @BindView(R.id.main2_sug_lvyou_res)
    TextView main2SugLY;
    @BindView(R.id.main2_sug_ziwaixian_res)
    TextView main2SugXWX;


    private Intent intent;
    private Intent changeTimeIntent ;

    private String weatherCache;

    private WeatherResp.WeatherInfo weatherInfo;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_UI:
                    refreshUI(weatherInfo);
                    main2RlLoading.setVisibility(View.INVISIBLE);
                    break;
                case UPDATE_UI_ERR:
                    //更新失败
                    main2RlLoading.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };
    private ArrayList<Integer> dataList;
    private ArrayList<String> bottomTextList;

    @Override
    protected void onStop() {
        super.onStop();
        main2RlLoading.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        ButterKnife.bind(main2_content_root);
        ButterKnife.bind(main2SugRoot);

        initData();
        initView();
    }

    //初始化数据
    private void initData() {

        dataList = new ArrayList<>();
        bottomTextList = new ArrayList<>();

        intent = new Intent(Main2Activity.this, UpdateService.class);
        changeTimeIntent = new Intent(Main2Activity.this, UpdateService.class);

        /* 1.检查缓存数据是否存在
         *     若存在则读取并更新UI
         *     不存在就检查网络
         *          网络不可用就提示用户打开网络
         *          可用就请求数据 更新UI
         *
         */
        // 1.检查缓存
        if(FileUtils.checkCache()){
            //缓存存在
            weatherCache = FileUtils.readCacheWeatherInfo();
            WeatherResp weatherResp = JsonParseUtils.parseJsonObject(weatherCache, WeatherResp.class);
            Log.i(TAG,"缓存："+weatherResp);
            weatherInfo = weatherResp.HeWeather5.get(0);
            //这里有数据
            handler.sendEmptyMessage(UPDATE_UI);

        }else{
            //缓存不存在
            //检查网络
            if(NetworkUtils.isNetworkAvailable() != -1){
                //网络可用
                InitDataUtils.initWeatherInfo(listener);
            }else{
                //网络不可用
                //提示用户网络不可用，
                Toast.makeText(Main2Activity.this,"网络不可用，请连接网络更新数据",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //处理初始化方法返回的结果
    private InitDataUtils.ResponseListener listener = new InitDataUtils.ResponseListener() {
        @Override
        public void handResponse(String result, int type) {

            Log.i(TAG, "handResponse: "+result);
            Log.i(TAG, "handResponse: "+type);

            //城市数据请求成功
            if(type == ActivityConstant.TYPE_CITY_INFO){
                //返回的结果是城市数据
                List<Citys> citysList = JsonParseUtils.parseJsonArray(result, Citys.class);

                for(int i=0;i<citysList.size();i++){
                    DBUtils.insert(citysList.get(i));
                }

                //城市数据存入数据库成功
                SharedPrefsUtil.save(DBConstant.KEY_BD_INITED,true);

            }else if(type == ActivityConstant.TYPE_WEATHER_INFO){
                //天气信息请求成功
                //保存天气信息
                FileUtils.saveWeatherInfoToCache(result);
                //解析天气信息
                WeatherResp weatherResp = JsonParseUtils.parseJsonObject(result, WeatherResp.class);
                //这个里面是一个集合，
                weatherInfo = weatherResp.HeWeather5.get(0);

                Log.i(TAG, "handResponse: weatherInfo = "+weatherInfo);

                handler.sendEmptyMessage(UPDATE_UI);
                Log.i(TAG, "handResponse: 发送消息给handler更新ui");

            }else{
                handler.sendEmptyMessage(UPDATE_UI_ERR);
            }
        }
    };

    //刷新界面
    private void refreshUI(WeatherResp.WeatherInfo info){

        // 中间显示天气状况图片：main2_iv_weatherIcon
        Picasso.with(MyApplication.getContext()).load(NetworkConstant.URL_WEATHER_ICON+info.now.cond.code+".png")
                .into(main2IvWeaIcon);
        // 天气状况 文字 main2_tv_weatherInfo
        main2TvWeaChs.setText(info.now.cond.txt);

        //更新时间 main2_tv_updateTime
        main2TvUpdateTime.setText("更新:"+info.basic.update.loc);

        // aqi main_tv_aqi main2_cv_aqi
        if(info.aqi!=null){
            main2Tvaqi.setText("空气质量 "+info.aqi.city.qlty+":"+info.aqi.city.aqi);
        }else{
            main2Cvaqi.setVisibility(View.INVISIBLE);
        }

        //输入位置信息
        main2TvLoc.setText(info.basic.city);

        //预警 main_tv_warn main2_cv_warn 当前天气api不支持
        main2CvWarm.setVisibility(View.INVISIBLE);

        if(info.now!=null){
            //湿度 main2_tv_humidity
            main2TvHumidity.setText("湿度:"+info.now.hum+"%");
            //能见度 main2_tv_visibility
            main2TvVisible.setText("能见度:"+info.now.vis+"km");
        }

        //日落 main2_tv_sunset
        WeatherResp.DailyForecast.SunriseSunsetTime astro = info.daily_forecast.get(0).astro;
        main2TvSunset.setText("日落:"+astro.ss);
        //日出 main2_tv_sunrise
        main2TvSunrise.setText("日出:"+astro.sr);

        //一天中温度变化 main2_hv_temp
        //每次刷新前将数据清空
        dataList.clear();
        bottomTextList.clear();
        //将数据提取出来
        int max = 0;
        List<WeatherResp.HourlyForecast> hourly_forecast = info.hourly_forecast;
        for (WeatherResp.HourlyForecast hour:hourly_forecast) {
            int temp = Integer.parseInt(hour.tmp);
            dataList.add(temp);
            bottomTextList.add(hour.date.substring(hour.date.lastIndexOf(" ")));
            if(temp>max){
                max = temp;
            }
        }
        
        main2HvTemp.setData(dataList,bottomTextList,max+5);
    
        //填充几个生活指数数据
        /*  public DetailSuggsetion air;//空气指数
            public DetailSuggsetion comf;//舒适度指数
            public DetailSuggsetion cw;//洗车指数
            public DetailSuggsetion drsg;//穿衣指数
            public DetailSuggsetion flu;//感冒指数
            public DetailSuggsetion sport;//运动指数
            public DetailSuggsetion trav;//旅游指数
            public DetailSuggsetion uv;//紫外线指数*/

       /* String spliteStr = ",";
        if(info.suggestion.comf.txt.indexOf(spliteStr) == -1){
            spliteStr = "，";
        }

        String test = "天气较好，但丝毫不会影响您出行的心情。";
        String[] strings = test.split("，");
        for(int i=0;i<strings.length;i++){
            Log.i("test",i+":"+strings[i]);
        }

        Log.i("testIndexof",""+test.indexOf("，"));*/

        //没有简介的时候
        if(TextUtils.isEmpty(info.suggestion.comf.bref)){
            main2SugSSD.setText(info.suggestion.comf.txt.split("，")[0]);
            main2SugXC.setText(info.suggestion.cw.txt.split("，")[0]);
            main2SugCY.setText(info.suggestion.drsg.txt.split("，")[0]);
            main2SugGM.setText(info.suggestion.flu.txt.split("，")[0]);
            main2SugYD.setText(info.suggestion.sport.txt.split("，")[0]);
            main2SugLY.setText(info.suggestion.trav.txt.split("，")[0]);
            main2SugXWX.setText(info.suggestion.uv.txt.split("，")[0]);
        }else{
            main2SugSSD.setText(info.suggestion.comf.bref);
            main2SugXC.setText(info.suggestion.cw.bref);
            main2SugCY.setText(info.suggestion.drsg.bref);
            main2SugGM.setText(info.suggestion.flu.bref);
            main2SugYD.setText(info.suggestion.sport.bref);
            main2SugLY.setText(info.suggestion.trav.bref);
            main2SugXWX.setText(info.suggestion.uv.bref);
        }
    }

    private void initView() {
        //高斯模糊
        Bitmap bitmapOri = BitmapFactory.decodeResource(getResources(), R.drawable.static_02);
        Bitmap newBitmap = StackBlur.blurNatively(bitmapOri, (int)
                20, false);
        //将模糊后的bitmap设置为背景
        main2IvBg.setImageBitmap(newBitmap);

        main2LlLoc.setOnClickListener(this);

        main2RlLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main2_ll_location:
                //点击弹出选择地区
                Intent intentSelectLoc = new Intent(Main2Activity.this,SelectLocActivity.class);
                startActivityForResult(intentSelectLoc,REQUEST_CODE_SELECT_LOC);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: 1");
        if(requestCode == REQUEST_CODE_SELECT_LOC){
            if(resultCode == RESULT_OK){
                //选择地点的Act已经返回数据过来了。
                //传过来的数据
                String city = data.getStringExtra("select_loc_data");
                String cityEngName = DBUtils.getCityEngName(city);
                if(TextUtils.isEmpty(cityEngName)){
                    Toast.makeText(this,"请选择正确的城市名称",Toast.LENGTH_SHORT).show();
                   return;
                }
                //请求数据
                InitDataUtils.initWeatherInfo(cityEngName,listener);
            }
        }
    }
}
