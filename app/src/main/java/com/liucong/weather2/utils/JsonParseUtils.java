package com.liucong.weather2.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by liucong on 2017/3/9.
 * 解析Json数据的类
 */

public class JsonParseUtils {
    //解析JsonArray
    public static <T> List<T> parseJsonArray(String json,Class<T> clazz){
        return JSON.parseArray(json,clazz);
    }

    //解析jsonObject
    public static <T> T parseJsonObject(String json,Class<T> clazz){
        Gson gson = new Gson();
        return gson.fromJson(json,clazz);
    }
}
