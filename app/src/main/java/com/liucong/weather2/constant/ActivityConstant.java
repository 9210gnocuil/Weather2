package com.liucong.weather2.constant;

import com.liucong.weather2.utils.StreamUtils;

/**
 * Created by liucong on 2017/3/10.
 */

public class ActivityConstant {

    public static final int ANIMATION_DURATION = 500;

    public static final int TYPE_CITY_INFO = 1;
    public static final int TYPE_WEATHER_INFO = 2;
    public static final int TYPE_CITY_INFO_ERR = 3;
    public static final int TYPE_WEATHER_INFO_ERR = 4;

    public static final String STRING_DEFAULT_CITY = "default_City";



    public static final String BROADCAST_CACHE_WEAHTER_DATA = "receiver_weather_cache";



    //网络状况
    public static final String NETWORK_STATE = "state_network";
    public static final int CODE_NETWORK_OK = 1000;
    public static final int CODE_NETWORK_NON = 1001;

    //数据库状态
    public static final String DATABASE_STATE = "state_database";
    public static final int CODE_DATABASE_OK = 2000;
    public static final int CODE_DATABASE_NON = 2001;

    //缓存天气信息状态
    public static final String WEATHER_CACHE_STATE = "state_weather_cache";
    public static final int WEATHER_CACHE_OK = 3000;
    public static final int WEATHER_CACHE_NON = 3001;
    public static final String WEATHER_CACHE_DATA = "data_value";

    public static final String CACHE_WEATHER_FILENAME_IN_DES = "cacheWeatherInfo.txt";
    public static final String CACHE_WEATHER_LOC = "/data/data/com.liucong.weather2/files/cacheWeatherInfo.txt";

    public static final String DATABASE_NAME_CITY = "city.db";
    public static final String DATABASE_LOC_CITY = "data/data/com.liucong.weather2/databases/city.db";
}
