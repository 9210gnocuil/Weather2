package com.liucong.weather2.utils;

import android.content.Context;
import android.graphics.Point;

import com.liucong.weather2.other.MyApplication;

/**
 * Created by liucong on 2017/3/12.
 */

public class DisplayUtils {

    public static int getScreenWidth(){
        return MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight(){
        return MyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int dp2px(int dp){
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static Point getDisplayScreen(){
        Point point = new Point();
        point.x = MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
        point.y = MyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
        return point;
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = MyApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = MyApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
