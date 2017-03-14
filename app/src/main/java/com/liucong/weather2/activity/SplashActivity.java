package com.liucong.weather2.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.liucong.weather2.R;
import com.liucong.weather2.bean.Citys;
import com.liucong.weather2.constant.ActivityConstant;
import com.liucong.weather2.constant.DBConstant;
import com.liucong.weather2.utils.DBUtils;
import com.liucong.weather2.utils.FileUtils;
import com.liucong.weather2.utils.InitDataUtils;
import com.liucong.weather2.utils.JsonParseUtils;
import com.liucong.weather2.utils.NetworkUtils;
import com.liucong.weather2.utils.SharedPrefsUtil;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 这个页面用于：
 * 1.初始化数据库
 * 2.请求天气信息
 *      如果这个页面天气信息请求失败了 则到了MainActiviy需要重新请求一次。
 *
 *
 * //如果有网络则第一次就需要将数据库完善，因此提示用户第一次进入需要花点时间
 * //实际上数据库的创建以及存储数据很花时间，所以在主页就不等了，直接拿到天气信息后到主页面上，
 * 因为数据库操作如果性能不算好的手机大概需要10多秒，毕竟要插入近3000条数据呢
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @BindView(R.id.splash_iv_bg)
    ImageView splashIvBg;
    private AnimationSet animSet;

    private Handler handler = new Handler();
    private long startTime;

    Intent intoMainActivityIntent;

    private boolean isCacheFileExist = false;
    private boolean isCityDBExist = false;
    private boolean isNetwordAvailable = false;

    //到下一个页面的Runnable
    private Runnable intoMainActivityRunnable = new Runnable() {
        @Override
        public void run() {
            SplashActivity.this.startActivity(intoMainActivityIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        startTime = System.currentTimeMillis();

        initView();

        initData();

    }

    private void initView() {

        ScaleAnimation scale = new ScaleAnimation(0.1f,1.0f,
                0.1f,1.0f,
                ScaleAnimation.RELATIVE_TO_SELF,0.5f,
                ScaleAnimation.RELATIVE_TO_SELF,0.5f);
        scale.setDuration(ActivityConstant.ANIMATION_DURATION);

        AlphaAnimation alpha = new AlphaAnimation(0.2f,1.0f);
        alpha.setDuration(ActivityConstant.ANIMATION_DURATION);

        animSet = new AnimationSet(false);
        animSet.addAnimation(scale);
        animSet.addAnimation(alpha);

        splashIvBg.startAnimation(animSet);
    }

    private void initData() {

        intoMainActivityIntent = new Intent(this,Main2Activity.class);

        //启动后应该检查本地有没有缓存天气信息 以及城市数据库是否已存在
        boolean[] cacheAndDatabase = checkCacheAndDatabase();

        //存在为true 不存在为false
        isCityDBExist = cacheAndDatabase[1];
        isCacheFileExist = cacheAndDatabase[0];

        //首先检查网路情况
        switch (NetworkUtils.isNetworkAvailable()){
            case -1:
                isNetwordAvailable = false;
                Log.i(TAG, "无网络");

                intoMainActivityIntent.putExtra(ActivityConstant.NETWORK_STATE,ActivityConstant.CODE_NETWORK_NON);
                handler.postDelayed(intoMainActivityRunnable,3000-(System.currentTimeMillis()-startTime));

                break;
            default:
                //初始化数据库
                //创建数据库
                //将初始化数据提取出来，以后会进场用的

                Log.i(TAG, "有网络");
                if(!isCityDBExist) {
                    Log.i(TAG, "数据库不存在1");
                    InitDataUtils.initDatabase(listener);
                    //initDatabase();
                }

                //请求天气
                if(!isCacheFileExist){
                    Log.i(TAG, "天气缓存不存在1");
                    //第一次 默认请求北京的天气
                    InitDataUtils.initWeatherInfo("beijing",listener);
                    //initWeatherInfo();
                }

                break;
        }

        if(isCacheFileExist){
            //缓存数据已经存在
            //进入主页面
            handler.postDelayed(intoMainActivityRunnable,3000);
        }
    }
    /**
     * 检查数据库是否已经存在 以及是否有天气信息缓存
     */
    private boolean[] checkCacheAndDatabase() {
        boolean[] boolArr = new boolean[2];
        boolArr[0] = FileUtils.checkCache();
        boolArr[1] = FileUtils.checkDatabase();
        return boolArr;
    }
    public int completedCount = 0;
    private InitDataUtils.ResponseListener listener = new InitDataUtils.ResponseListener() {
        @Override
        public void handResponse(String result, int type) {

            completedCount++;

            Log.i(TAG, "handResponse: completedCount"+completedCount);
            if(type == ActivityConstant.TYPE_CITY_INFO){
                Log.i(TAG, "handResponse: SplashActivity"+"请求城市信息成功");
                //返回的结果是城市数据

                //这个方法是在子线程中运行了 直接不管他，让他在后台运行就好了。
                List<Citys> citysList = JsonParseUtils.parseJsonArray(result, Citys.class);
                for(int i=0;i<citysList.size();i++){
                    DBUtils.insert(citysList.get(i));
                }

                SharedPrefsUtil.save(DBConstant.KEY_BD_INITED,true);

            }else if(type == ActivityConstant.TYPE_WEATHER_INFO){
                Log.i(TAG, "handResponse: SplashActivity"+"请求天气信息成功");

                //将服务器返回的结果存到用户目录下面
                FileUtils.saveWeatherInfoToCache(result);

                //请求到了消息后到主页去
                handler.post(intoMainActivityRunnable);
            }
        }
    };

}
