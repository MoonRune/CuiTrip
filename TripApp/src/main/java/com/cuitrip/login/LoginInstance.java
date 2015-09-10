package com.cuitrip.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.UserConfig;
import com.cuitrip.app.rong.RongCloudEvent;
import com.cuitrip.model.UserInfo;
import com.lab.utils.LogHelper;

public class LoginInstance {


    static final String KEY_USERINFO_ID = "_user_info";

    private static LoginInstance sInstance;
    private UserInfo sUserInfo;

    private LoginInstance() {
    }

    public synchronized static LoginInstance getInstance(Context context) {
        if (context == null) context = MainApplication.getInstance();
        if (sInstance == null) {
            sInstance = new LoginInstance();
            sInstance.read(context);
        }
        return sInstance;
    }

    private void read(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_USERINFO_ID, Context.MODE_PRIVATE);
        String userInfo = sp.getString(KEY_USERINFO_ID, null);
        if (userInfo != null) {
            sUserInfo = JSON.parseObject(userInfo, UserInfo.class);
        }
    }

    public static void update(Context context, UserInfo userInfo) {
        LoginInstance info = getInstance(context);
        if (info.sUserInfo != null && !TextUtils.isEmpty(info.sUserInfo.getUid()) && userInfo != null && !TextUtils.isEmpty(userInfo.getUid()) && info.sUserInfo.getUid().equals(userInfo.getUid())) {
            if (TextUtils.isEmpty(userInfo.getRongyunToken())) {
                userInfo.setRongyunToken(info.sUserInfo.getRongyunToken());
            }
        }
        info.sUserInfo = userInfo;
        SharedPreferences sp = context.getSharedPreferences(KEY_USERINFO_ID, Context.MODE_PRIVATE);
        if (userInfo != null) {
            LogHelper.e("omg", "save user info  :" + JSONObject.toJSON(userInfo).toString());

            sp.edit().putString(KEY_USERINFO_ID, JSONObject.toJSON(userInfo).toString())
                    .commit();
        } else {
            sp.edit().remove(KEY_USERINFO_ID).commit();
            UserConfig.clear();
        }
        LogHelper.e("omg", "update ed ");
        RongCloudEvent.ConnectRongForce();
        LogHelper.e("omg", "ConnectRongForce ");
    }

    public static void logout(Context context) {
        UserConfig.clear();
        SharedPreferences sp = context.getSharedPreferences(KEY_USERINFO_ID, Context.MODE_PRIVATE);
        sp.edit().remove(KEY_USERINFO_ID).commit();
        LoginInstance info = getInstance(context);
        info.sUserInfo = null;

    }

    public static boolean isLogin(Context context) {
        UserInfo info = getInstance(context).sUserInfo;
        if (info == null) {
            return false;
        }
        return !TextUtils.isEmpty(info.getToken());
    }

    public UserInfo getUserInfo() {
        return sUserInfo;
    }


}
