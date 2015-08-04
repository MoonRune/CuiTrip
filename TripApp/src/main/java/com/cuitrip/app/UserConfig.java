package com.cuitrip.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cuitrip.login.LoginInstance;

/**
 * Created by baziii on 15/7/28.
 */
public class UserConfig {
    public static final String USER_CONFIG=".user_config";
    public static final String PERSONAL_DESC="personal_desc";
    public static final String DEFAULT_STRING="";
    SharedPreferences sp;
    private String currentUserId;
    private static UserConfig config = null;

    public static UserConfig getInstance() {
        if (config == null) {
            synchronized (UserConfig.class) {
                if (config == null) {
                    config = new UserConfig();
                }
            }
        } else if (!TextUtils.isEmpty(config.currentUserId) && LoginInstance.getInstance(MainApplication.sContext).getUserInfo() != null &&
                !config.currentUserId.equals(LoginInstance.getInstance(MainApplication.sContext).getUserInfo().getUid())) {
            synchronized (UserConfig.class) {
                if (config == null) {
                    config = new UserConfig();
                }
            }

        }
        return config;
    }

    private UserConfig() {
        sp = MainApplication.sContext.getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE);
        currentUserId = LoginInstance.getInstance(MainApplication.sContext).getUserInfo().getUid();
    }

    public String getPersonalDesc(){
        return sp.getString(PERSONAL_DESC,DEFAULT_STRING);
    }
    public void setPersonalDesc(String text) {
        sp.edit().putString(PERSONAL_DESC,text).commit();
    }
}
