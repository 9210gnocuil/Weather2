package com.liucong.weather2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.liucong.weather2.bean.Citys;
import com.liucong.weather2.constant.DBConstant;
import com.liucong.weather2.db.CityDBOpenhelper;
import com.liucong.weather2.other.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liucong on 2017/3/10.
 */

public class DBUtils {

    //将城市信息插入到数据库中需要临时用到的变量
    public static String lastInsertProvinceName = "";
    public static int lastInsertProvinceId = -1;
    public static String lastInsertCityName = "";
    public static int lastInsertCityId =-1;

    public static SQLiteDatabase db;

    public static SQLiteDatabase getDatabase(){

        if(db == null){
            db = new CityDBOpenhelper(MyApplication.getContext()).getWritableDatabase();
        }
        return db;
    }

    public static List<String> queryAllProvince(){
        List<String> provinceList = new ArrayList<String>();

        Cursor provinceQuery = getDatabase().query(DBConstant.TABLE_PROVINCE, new String[]{DBConstant.COLUMN_PROVINCE_PROVINCECHS}, null, null, null, null, null);
        while (provinceQuery.moveToNext()){
            provinceList.add(provinceQuery.getString(0));
        }
        provinceQuery.close();

        return provinceList;


    }

    public static String getCityEngName(String cityChs){
        Cursor query = getDatabase().query(DBConstant.TABLE_COUNTY, new String[]{DBConstant.COLUMN_COUNTY_COUNTYENG}, DBConstant.COLUMN_COUNTY_COUNTYCHS + "=?", new String[]{cityChs}, null, null, null);
        if(query.moveToNext()){
            String cityEngName = query.getString(0);
            query.close();
            return  cityEngName;
        }

        return null;
    }

    public static int queryIndex(String tableName,String city){
        int index = -1;
        Cursor query = getDatabase().query(tableName, new String[]{"_id"}, getColumnNameFromTable(tableName) + "=?", new String[]{city}, null, null, null);
        if(query.moveToNext()){
            index = query.getInt(0);
        }
        query.close();
        return index;
    }

    public static List<String> queryAll(String cityName){
        //首先判断是在那张表里
        int id;
        //先查Province
        Cursor cursorProvince = getDatabase().query(DBConstant.TABLE_PROVINCE, new String[]{"_id"}, DBConstant.COLUMN_PROVINCE_PROVINCECHS + "=?", new String[]{cityName}, null, null, null);
        if(cursorProvince.moveToNext()){
            //Province里面有数据，为第一次点击
            id = cursorProvince.getInt(0);
            //返回此id的所有城市列表
            cursorProvince.close();
            return queryAllCity(id);
        }

        Cursor cursorCity = getDatabase().query(DBConstant.TABLE_CITY, new String[]{"_id"}, DBConstant.COLUMN_CITY_CITYCHS + "=?", new String[]{cityName}, null, null, null);
        if(cursorCity.moveToNext()){
            //Province里面有数据，为第一次点击
            id = cursorCity.getInt(0);
            //返回此id的所有城市列表
            cursorCity.close();
            return queryAllCounty(id);
        }

        //到了这里说明肯定已经显示了城镇列表了
        return null;

    }

    private static String getColumnNameFromTable(String tableName){
        switch (tableName){
            case DBConstant.TABLE_PROVINCE:
                return DBConstant.COLUMN_PROVINCE_PROVINCECHS;

            case DBConstant.TABLE_CITY:
                return DBConstant.COLUMN_CITY_CITYCHS;
            case DBConstant.TABLE_COUNTY:
                return DBConstant.COLUMN_COUNTY_COUNTYCHS;
            default:
                return null;
        }
    }

    public static List<String> queryAllCity(int provinceId){
        List<String> cityList = new ArrayList<String>();

        Cursor cityQuery = getDatabase().query(DBConstant.TABLE_CITY,
                new String[]{DBConstant.COLUMN_CITY_CITYCHS},
                DBConstant.COLUMN_CITY_LEADERPROVINCEID+"=?",
                new String[]{provinceId+""},null,null,null);
        while (cityQuery.moveToNext()){
            cityList.add(cityQuery.getString(0));
        }
        cityQuery.close();
        return cityList;

    }
    public static List<String> queryAllCounty(int cityId){
        List<String> countyList = new ArrayList<String>();

        Cursor countyQuery = getDatabase().query(DBConstant.TABLE_COUNTY, new String[]{DBConstant.COLUMN_COUNTY_COUNTYCHS},DBConstant.COLUMN_COUNTY_LEADERCITYID+"=?",new String[]{cityId+""},null,null,null);
        while (countyQuery.moveToNext()){
            countyList.add(countyQuery.getString(0));
        }
        countyQuery.close();
        return countyList;
    }

    public static void insert(Citys citys){

        String cityEn = citys.cityEn; //城市英文名
        String cityZh = citys.cityZh; //城市中文名
        String provinceEn = citys.provinceEn; //省英文
        String provinceZh = citys.provinceZh; //省中文
        String leaderEn = citys.leaderEn; //所属上级市英文名
        String leaderZh = citys.leaderZh;//所属上级市中文名
        String id = citys.id; //城市id

        /*数据添加
        1.首先插入省，记录当前的省的id 以及名字
        2.判断下一个插入的省是不是当前省
                --是：则执行后面操作
                --否：存入，并记录新的省索引(插入成功后会返回index)和名字
        3.插入市，记录当前市的id以及名字，
        4.判断下一个插入的市是不是当前市
                --是，执行后面操作
                --不是，存入并记录新的市索引(插入成功后会返回index)和名字
        5.插入县 插入县相关的信息，并且插入这个县的上级市的名字*/

        //插入省
        if(!provinceZh.equals(lastInsertProvinceName)){
            //省名不相同，没有插入过
            //1.现插入省
            ContentValues provinceValues = new ContentValues();
            provinceValues.put("provinceChs",provinceZh);
            provinceValues.put("provinceEng",provinceEn);
            lastInsertProvinceId = (int) getDatabase().insert("Province",null,provinceValues);
            lastInsertProvinceName = provinceZh;
        }

        //插入市
        if (!leaderZh.equals(lastInsertCityName)) {
            //市名不相同，没有插入过
            //2.现插入市
            //leaderProvinceId cityChs cityEng
            ContentValues cityValues = new ContentValues();
            cityValues.put("leaderProvinceId", lastInsertProvinceId);
            cityValues.put("cityChs", leaderZh);
            cityValues.put("cityEng", leaderEn);
            lastInsertCityId = (int) getDatabase().insert("City", null, cityValues);
            lastInsertCityName = leaderZh;
        }

        //插入县镇
        //countyCode leaderCityId countyChs countyEng
        ContentValues countyValues = new ContentValues();
        countyValues.put("countyCode", id);
        countyValues.put("leaderCityId", lastInsertCityId);
        countyValues.put("countyChs", cityZh);
        countyValues.put("countyEng", cityEn);
        getDatabase().insert("County", null, countyValues);

        Log.i("DBUtils", "插入城市:"+cityZh);
    }
}
