package com.liucong.weather2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liucong on 2017/3/9.
 *
 * 数据库结构 后面都是字段
 * --Province
 *      --provinceChs
 *      --provinceEng
 * --City
 *      --leaderProvinceId 该市的上级省的在数据库中的id
 *      --cityChs
 *      --cityEng
 * --County
 *      --countyCode 县镇的编号
 *      --leaderCityId 该县镇的上级市在数据库中的id
 *      --countyChs
 *      --countyEng
 */

public class CityDBOpenhelper extends SQLiteOpenHelper {

    private String createTableProvince = "create table Province (_id integer primary key autoincrement,provinceChs varchar,provinceEng varchar)";
    private String createTableCity = "create table City (_id integer primary key autoincrement,leaderProvinceId integer,cityChs varchar,cityEng varchar)";
    private String createTableCounty = "create table County (_id integer primary key autoincrement,countyCode varchar,leaderCityId integer,countyChs varchar,countyEng varchar)";

    public CityDBOpenhelper(Context context) {
        super(context, "city.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableProvince);
        db.execSQL(createTableCity);
        db.execSQL(createTableCounty);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
