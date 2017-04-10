package com.liucong.weather2.utils;

import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by liucong-w on 2017/4/9.
 */

public class HttpUtils {

    private static final String TAG = "HttpUtils";
    public interface RequestListener {
        void onSuccess(String result);

        void onFailure(Throwable e);
    }

    public static String request(String urlStr) throws IOException {

        Log.i(TAG, "正在请求网络");


        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }

            connection.disconnect();
            inputStream.close();
            bis.close();
            baos.close();

            //模拟延时操作
            Log.i(TAG, "网络请求成功");
            SystemClock.sleep(2000);
            return baos.toString();
        }
        return null;
    }
}
