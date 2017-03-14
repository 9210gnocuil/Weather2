package com.liucong.weather2.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by liucong on 2017/3/10.
 */

public class StreamUtils {

    /**
     * 关闭流
     * @param stream
     */
    public static void closeStream(Closeable stream){
        try {
            if( stream != null){
                stream.close();
                stream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
