package com.wenky.design.util;

import android.util.Log;

/**
 * Created by wl on 2018/12/5.
 */
public class LogHelper {

    private static final String TAG = LogHelper.class.getSimpleName();

    private static boolean isDebug = true;

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }
}
