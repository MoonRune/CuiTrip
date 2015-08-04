package com.lab.utils;

import android.content.Context;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 *
 * http://dev.umeng.com/analytics/android-doc/integration#2_1_4
 */
public class AnalyticsHelper {
    public static void setAppkey(String appkey){
        AnalyticsConfig.setAppkey(appkey);
    }
    public static void setChannel(String channel){
        AnalyticsConfig.setChannel(channel);
    }
    public static void openActivityDurationTrack(boolean open){
        MobclickAgent.openActivityDurationTrack(open);
    }

    /**
     * //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
     * @param name
     */
    public static void onPageStart(String name) {
        MobclickAgent.onPageStart(name);
    }

    /**
     * //统计时长
     * @param context
     */
    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    /**
     * // （仅有Activity的应用中SDK自动调用，不需要单独写）
     * 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
     * @param name
     */
    public static void onPageEnd(String name){
        MobclickAgent.onPageStart(name); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
    }

    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }
    public static void updateOnlineConfig(Context context) {
        MobclickAgent.updateOnlineConfig(context);
    }
    public static void enableEncrypt(boolean enable) {
        AnalyticsConfig.enableEncrypt(enable);
    }

    public static void setDebugMode(boolean enable) {
        MobclickAgent.setDebugMode(enable);
    }

}
