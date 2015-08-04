package com.lab.utils;

import android.util.Log;

import com.cuitrip.app.MainApplication;

/**
 * Created on 7/8.
 */
public class LogHelper {

    public static final String TAG = "CuiTrip-";

    public static int v(String tag, String msg) {
        if (!MainApplication.IS_DEV) {
            return 0;
        }
        return Log.v(TAG + tag, msg);
    }

    public static int d(String tag, String msg) {
        if (!MainApplication.IS_DEV) {
            return 0;
        }
        return Log.d(TAG + tag, msg);
    }

    public static int i(String tag, String msg) {
        if (!MainApplication.IS_DEV) {
            return 0;
        }
        return Log.i(TAG + tag, msg);
    }

    public static int w(String tag, String msg) {
        if (!MainApplication.IS_DEV) {
            return 0;
        }
        return Log.w(TAG + tag, msg);
    }

    public static int e(String tag, String msg) {
        if (!MainApplication.IS_DEV) {
            return 0;
        }
        return Log.e(TAG + tag, msg);
    }
}
