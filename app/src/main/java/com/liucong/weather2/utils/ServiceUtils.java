package com.liucong.weather2.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.liucong.weather2.other.MyApplication;

import java.util.List;

/**
 * Created by liucong on 2017/3/15.
 */

public class ServiceUtils {
    public static List<ActivityManager.RunningServiceInfo> getRunningServiceList(){
        ActivityManager am = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);

        return  am.getRunningServices(50);

    }
    public static boolean isServiceRunning(String serviceName){

        boolean result = false;
        List<ActivityManager.RunningServiceInfo> runningServiceList = getRunningServiceList();
        for (ActivityManager.RunningServiceInfo info:runningServiceList) {
            String className = info.service.getClassName();
            //com.baidu.netdisk.service.NetdiskService
            if(className.equals(serviceName)){
                result = true;
                break;
            }
        }
        return result;
    }
}
