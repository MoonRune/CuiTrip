package com.cuitrip.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cuitrip.app.MainApplication;
import com.cuitrip.model.UserInfo;

public class LoginInstance {
    static final String KEY_USERINFO_ID = "_user_info";

    private static LoginInstance sInstance;
    private UserInfo sUserInfo;

    private LoginInstance() {
    }

    public synchronized static LoginInstance getInstance(Context context) {
        if (context == null) context = MainApplication.sContext;
        if (sInstance == null) {
            sInstance = new LoginInstance();
            sInstance.read(context);
        }
        return sInstance;
    }

    private void read(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_USERINFO_ID, Context.MODE_PRIVATE);
        String userInfo = sp.getString(KEY_USERINFO_ID, null);
        if(userInfo != null){
            sUserInfo = JSON.parseObject(userInfo, UserInfo.class);
        }
    }

    public static void update(Context context, UserInfo userInfo) {
        LoginInstance info = getInstance(context);
        info.sUserInfo = userInfo;
        SharedPreferences sp = context.getSharedPreferences(KEY_USERINFO_ID, Context.MODE_PRIVATE);
        if (userInfo != null) {
            sp.edit().putString(KEY_USERINFO_ID, JSONObject.toJSON(userInfo).toString())
                    .commit();
        } else {
            sp.edit().remove(KEY_USERINFO_ID).commit();
        }
    }

    public static void logout(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_USERINFO_ID, Context.MODE_PRIVATE);
        sp.edit().remove(KEY_USERINFO_ID).commit();
        LoginInstance info = getInstance(context);
        info.sUserInfo = null;
    }

    public static boolean isLogin(Context context) {
        UserInfo info = getInstance(context).sUserInfo;
        if(info == null){
            return false;
        }
        return !TextUtils.isEmpty(info.getToken());
    }

    public UserInfo getUserInfo() {
        return sUserInfo;
    }
}
