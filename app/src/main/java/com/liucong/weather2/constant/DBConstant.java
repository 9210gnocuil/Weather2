package com.liucong.weather2.constant;

/**
 * Created by liucong on 2017/3/10.
 */

public class DBConstant {

    //三个表名
    public static final String TABLE_PROVINCE = "Province";
    public static final String TABLE_CITY = "City";
    public static final String TABLE_COUNTY = "County";


    //Privince表里面的字段
    public static final String COLUMN_PROVINCE_PROVINCECHS = "provinceChs";
    public static final String COLUMN_PROVINCE_PROVINCEENG = "provinceEng";

    //City表里面的字段
    public static final String COLUMN_CITY_LEADERPROVINCEID = "leaderProvinceId";
    public static final String COLUMN_CITY_CITYCHS = "cityChs";
    public static final String COLUMN_CITY_CITYENG = "cityEng";

    //County表里面的字段
    public static final String COLUMN_COUNTY_COUNTYCODE = "countyCode";
    public static final String COLUMN_COUNTY_LEADERCITYID = "leaderCityId";
    public static final String COLUMN_COUNTY_COUNTYCHS = "countyChs";
    public static final String COLUMN_COUNTY_COUNTYENG = "countyEng";


    public static final String KEY_BD_INITED = "database_inited";
}
