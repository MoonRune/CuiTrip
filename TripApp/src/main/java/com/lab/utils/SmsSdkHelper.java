package com.lab.utils;

import android.content.Context;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Createdon 7/8.
 * http://wiki.mob.com/android-%E7%9F%AD%E4%BF%A1sdk%E6%93%8D%E4%BD%9C%E5%9B%9E%E8%B0%83/
 */
public class SmsSdkHelper {
    public static final String TAG = "SmsSdkHelper";

    public static void initSmsSDK(Context context) {
        SMSSDK.initSDK(context, "8bd83bd3d601", "5b0968795e5c9e95bac36a507b3cd5f0");
    }

    public static void registerEventHandler(EventHandler eventHandler) {
        SMSSDK.registerEventHandler(eventHandler);
    }

    public static void unregisterEventHandler(EventHandler eventHandler) {
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    public static void getSupportedCountries(){
        SMSSDK.getSupportedCountries();
    }

    EventHandler eh = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                LogHelper.d(TAG, "afterEvent success");
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                LogHelper.d(TAG, "afterEvent error");
                ((Throwable) data).printStackTrace();
            }
        }
        public void onRegister() {
            LogHelper.d(TAG, "onRegister success");
        }

        public void beforeEvent(int var1, Object var2) {
        }

        public void onUnregister() {
            LogHelper.d(TAG, "onUnregister success");
        }
    };

    public static void getVerificationCode(String country, String phone){
        SMSSDK.getVerificationCode(country, phone);
    }
}
