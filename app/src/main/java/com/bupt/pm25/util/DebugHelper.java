package com.bupt.pm25.util;

import android.util.Log;

/**
 * Created by hougr on 15/11/27.
 */
public class DebugHelper {
    public static final boolean LOGGER_ON_BOOLEAN =false;

    public static void logD(String debugString){
        if(LOGGER_ON_BOOLEAN){
            Log.d("我的Tag", debugString);
        }

    }
}
