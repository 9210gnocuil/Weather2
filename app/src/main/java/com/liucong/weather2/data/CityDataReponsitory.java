package com.liucong.weather2.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.liucong.weather2.other.MyApplication;
import com.liucong.weather2.utils.SharedPrefsUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liucong-w on 2017/4/10.
 * 城市信息
 */

public class CityDataReponsitory {

    private static final String TAG = "CityDataReponsitory";

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

    public static SQLiteDatabase mDB;
    public static File mDatabaseFilePath = new File(MyApplication.getContext().getFilesDir(),"city.db");;

    public static void copyDb(final String name){
        //拷贝数据库
        new Thread(){
            @Override
            public void run() {
                InputStream is = null;
                try {
                    is = MyApplication.getContext().getAssets().open(name);
                } catch (IOException e) {
                    //如果打开出错了 就直接返回
                    return;
                }
                //检查是否文件是否存在
                File file = new File(MyApplication.getContext().getFilesDir(),name);
                if (file.exists()) {
                    //提示已拷贝
                    Log.i(TAG,"数据库已存在");
                }else{
                    //创建并复制
                    BufferedOutputStream bos = null;
                    BufferedInputStream bis = null;
                    try{
                        file.createNewFile();
                        try {
                            bos = new BufferedOutputStream(new FileOutputStream(file));
                        } catch (FileNotFoundException e) {
                            return;
                        }
                        bis = new BufferedInputStream(is);

                        byte[] buf = new byte[1024];
                        int length = 0;
                        while( (length = bis.read(buf)) != -1 ) {
                            bos.write(buf, 0, length);
                        }
                        bos.flush();
                        //提示用户已拷贝成功
                        Log.i(TAG,"数据库拷贝成功");
                    }catch(IOException e) {
                        e.printStackTrace();
                        Log.i(TAG,"数据库拷贝失败");
                    }finally {
                        try{
                            if(bis != null)
                                bis.close();
                            if (bos != null){
                                bos.close();
                            }

                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    public static String getDefaultCity(){
        return SharedPrefsUtil.get("defaultCity",null);
    }
    public static void setDefaultCity(String city){
        SharedPrefsUtil.save("defaultCity",city);
    }

    public static SQLiteDatabase getDB(){
        if(mDB == null){
            synchronized (CityDataReponsitory.class){
                if(mDB==null){
                    mDB = SQLiteDatabase.openDatabase(mDatabaseFilePath.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY);
                }
            }
        }
        return mDB;
    }

    public static List<String> queryAllProvince(){
        List<String> provinceList = new ArrayList<String>();

        Cursor provinceQuery = getDB().query(TABLE_PROVINCE, new String[]{COLUMN_PROVINCE_PROVINCECHS}, null, null, null, null, null);
        while (provinceQuery.moveToNext()){
            provinceList.add(provinceQuery.getString(0));
        }
        provinceQuery.close();

        return provinceList;
    }

    public static List<String> queryAll(String cityName){
        //首先判断是在那张表里
        int id;
        //先查Province
        Cursor cursorProvince = getDB().query(TABLE_PROVINCE, new String[]{"_id"}, COLUMN_PROVINCE_PROVINCECHS + "=?", new String[]{cityName}, null, null, null);
        if(cursorProvince.moveToNext()){
            //Province里面有数据，为第一次点击
            id = cursorProvince.getInt(0);
            //返回此id的所有城市列表
            cursorProvince.close();
            return queryAllCity(id);
        }

        Cursor cursorCity = getDB().query(TABLE_CITY, new String[]{"_id"}, COLUMN_CITY_CITYCHS + "=?", new String[]{cityName}, null, null, null);
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

    public static List<String> queryAllCity(int provinceId){
        List<String> cityList = new ArrayList<String>();

        Cursor cityQuery = getDB().query(TABLE_CITY,
                new String[]{COLUMN_CITY_CITYCHS},
                COLUMN_CITY_LEADERPROVINCEID+"=?",
                new String[]{provinceId+""},null,null,null);
        while (cityQuery.moveToNext()){
            cityList.add(cityQuery.getString(0));
        }
        cityQuery.close();
        return cityList;

    }
    public static List<String> queryAllCounty(int cityId){
        List<String> countyList = new ArrayList<String>();

        Cursor countyQuery = getDB().query(TABLE_COUNTY, new String[]{COLUMN_COUNTY_COUNTYCHS},COLUMN_COUNTY_LEADERCITYID+"=?",new String[]{cityId+""},null,null,null);
        while (countyQuery.moveToNext()){
            countyList.add(countyQuery.getString(0));
        }
        countyQuery.close();
        return countyList;
    }
}
