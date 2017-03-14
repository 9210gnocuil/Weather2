package com.liucong.weather2.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by liucong on 2017/3/10.
 */

public class WeatherResp {

    public List<WeatherInfo> HeWeather5;

    @Override
    public String toString() {
        return "WeatherResp{" +
                "HeWeather5=" + HeWeather5 +
                '}';
    }

    public class WeatherInfo{
        public AirQualityIndex aqi;
        public BasicInfo basic;
        public List<DailyForecast> daily_forecast;
        public List<HourlyForecast> hourly_forecast;
        public NowForecast now;
        public Suggestion suggestion;
        public String status;

        @Override
        public String toString() {
            return "WeatherInfo{" +
                    "aqi=" + aqi +
                    ", basic=" + basic +
                    ", daily_forecast=" + daily_forecast +
                    ", hourly_forecast=" + hourly_forecast +
                    ", now=" + now +
                    ", suggestion=" + suggestion +
                    ", status='" + status + '\'' +
                    '}';
        }
    }


    public class Suggestion{
        //生活指数
        public DetailSuggsetion air;//空气指数
        public DetailSuggsetion comf;//舒适度指数
        public DetailSuggsetion cw;//洗车指数
        public DetailSuggsetion drsg;//穿衣指数
        public DetailSuggsetion flu;//感冒指数
        public DetailSuggsetion sport;//运动指数
        public DetailSuggsetion trav;//旅游指数
        public DetailSuggsetion uv;//紫外线指数

        @Override
        public String toString() {
            return "Suggestion{" +
                    "air=" + air +
                    ", comf=" + comf +
                    ", cw=" + cw +
                    ", drsg=" + drsg +
                    ", flu=" + flu +
                    ", sport=" + sport +
                    ", trav=" + trav +
                    ", uv=" + uv +
                    '}';
        }

        public class DetailSuggsetion{
            public String bref;//简介
            public String txt;//详细描述

            @Override
            public String toString() {
                return "DetailSuggsetion{" +
                        "bref='" + bref + '\'' +
                        ", txt='" + txt + '\'' +
                        '}';
            }
        }
    }

    public class AirQualityIndex{

        public AirQualityInfo city;

        @Override
        public String toString() {
            return "AirQualityIndex{" +
                    "city=" + city +
                    '}';
        }

        public class AirQualityInfo{

            public String aqi;//空气质量指数
            public String co;//一氧化碳1小时平均值(ug/m³)
            public String no2;//二氧化氮1小时平均值(ug/m³)
            public String o3;//臭氧1小时平均值(ug/m³)
            public String pm10;//PM10 1小时平均值(ug/m³)
            public String pm25;//PM2.5 1小时平均值(ug/m³)
            public String qlty;//空气质量类别，共六个级别，分别：优，良，轻度污染，中度污染，重度污染，严重污染
            public String so2;//二氧化硫1小时平均值(ug/m³)

            @Override
            public String toString() {
                return "AirQualityInfo{" +
                        "aqi='" + aqi + '\'' +
                        ", co='" + co + '\'' +
                        ", no2='" + no2 + '\'' +
                        ", o3='" + o3 + '\'' +
                        ", pm10='" + pm10 + '\'' +
                        ", pm25='" + pm25 + '\'' +
                        ", qlty='" + qlty + '\'' +
                        ", so2='" + so2 + '\'' +
                        '}';
            }
        }
    }

    public class BasicInfo{

        public String city;//城市名称

        public String cnty;//国家
        public String id;//城市ID
        public String lat;//城市维度
        public String lon;//城市经度
        public UpdateTime update;//更新时间

        @Override
        public String toString() {
            return "BasicInfo{" +
                    "city='" + city + '\'' +
                    ", cnty='" + cnty + '\'' +
                    ", id='" + id + '\'' +
                    ", lat='" + lat + '\'' +
                    ", lon='" + lon + '\'' +
                    ", update=" + update +
                    '}';
        }

        public class UpdateTime{
            public String loc;//当地时间
            public String utc;//UTC时间
        }
    }

    public class DailyForecast{

        public SunriseSunsetTime astro;
        public DailyWeatherCondition cond;
        public String date;//预报日期
        public String hum;//相对湿度（%）
        public String pcpn;//降水量（mm）
        public String pop;//降水概率
        public String pres;//气压
        public TemperatureRange tmp;
        public String vis;//能见度
        public WindInfo wind;//风力风向


        @Override
        public String toString() {
            return "DailyForecast{" +
                    "astro=" + astro +
                    ", cond=" + cond +
                    ", date='" + date + '\'' +
                    ", hum='" + hum + '\'' +
                    ", pcpn='" + pcpn + '\'' +
                    ", pop='" + pop + '\'' +
                    ", pres='" + pres + '\'' +
                    ", tmp=" + tmp +
                    ", vis='" + vis + '\'' +
                    ", wind=" + wind +
                    '}';
        }

        public class SunriseSunsetTime{
            public String mr;//月升时间
            public String ms;//月落时间
            public String sr;//日出时间
            public String ss;//日落时间
        }

        public class DailyWeatherCondition{
            public String code_d;//白天天气状况代码
            public String code_n;//夜间天气状况代码
            public String txt_d;//白天天气状况描述
            public String txt_n;//夜间天气状况描述
        }

        //温度范围
        public class TemperatureRange{
            public String max;
            public String min;
        }

        //风速信息
        public class WindInfo{
            public String deg;//风向（360度）
            public String dir;//风向
            public String sc; //风力等级
            public String spd;//风速（kmph）
        }
    }

    public class HourlyForecast{

        public HourlyWeatherCondition cond;
        public String date;//预报日期
        public String hum;//相对湿度（%）
        public String pop;//降水概率
        public String pres;//气压
        public String tmp;
        public WindInfo wind;

        @Override
        public String toString() {
            return "HourlyForecast{" +
                    "cond=" + cond +
                    ", date='" + date + '\'' +
                    ", hum='" + hum + '\'' +
                    ", pop='" + pop + '\'' +
                    ", pres='" + pres + '\'' +
                    ", tmp='" + tmp + '\'' +
                    ", wind=" + wind +
                    '}';
        }

        public class HourlyWeatherCondition{
            public String code;//天气状况代码
            public String txt;//天气状况描述

        }

        //风速信息
        public class WindInfo{
            public String deg;//风向（360度）
            public String dir;//风向
            public String sc; //风力等级
            public String spd;//风速（kmph）
        }
    }

    public class NowForecast{

        public NowWeatherCondition cond;
        public String fl;
        public String hum;//相对湿度（%）
        public String pcpn;
        public String pres;//气压
        public String tmp;
        public String vis;
        public WindInfo wind;

        @Override
        public String toString() {
            return "NowForecast{" +
                    "cond=" + cond +
                    ", fl='" + fl + '\'' +
                    ", hum='" + hum + '\'' +
                    ", pcpn='" + pcpn + '\'' +
                    ", pres='" + pres + '\'' +
                    ", tmp='" + tmp + '\'' +
                    ", vis='" + vis + '\'' +
                    ", wind=" + wind +
                    '}';
        }

        public class NowWeatherCondition{
            public String code;//天气状况代码
            public String txt;//天气状况描述

        }

        //风速信息
        public class WindInfo{
            public String deg;//风向（360度）
            public String dir;//风向
            public String sc; //风力等级
            public String spd;//风速（kmph）
        }
    }
}
