package com.cuitrip.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.login.LoginInstance;
import com.lab.utils.SavedDescSharedPreferences;

/**
 * Created by baziii on 15/7/28.
 */
public class UserConfig {


    public static final String USER_CONFIG = ".user_config";
    public static final String PERSONAL_DESC = "personal_desc";

    static final String KEY_PAYPAL_ACCOUNT_ID = "_paypal_account";
    static final String KEY_UNITINFO_ID = "_user_unit_info";

    public static final String DEFAULT_STRING = "";
    SharedPreferences sp;
    private String currentUserId;
    private static UserConfig config = null;

    public static void clear() {
        SharedPreferences about = getInstance().sp;
        about.edit().clear().commit();
        SavedDescSharedPreferences.deleteServiceDesc(MainApplication.getInstance());
    }
    public static void clear(UserConfig config) {
        SharedPreferences about = config.sp;
        about.edit().clear().commit();
        SavedDescSharedPreferences.deleteServiceDesc(MainApplication.getInstance());
    }

    public static UserConfig getInstance() {
        if (config == null) {
            synchronized (UserConfig.class) {
                if (config == null) {
                    config = new UserConfig();
                }
            }
        } else {
           //当前config存在的情况下
            // current 不为空  id不同 ，重建
            synchronized (UserConfig.class) {
                clear(config);
                config = new UserConfig();
            }

        }
        return config;
    }

    private UserConfig() {
        sp = MainApplication.getInstance().getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE);
        if (LoginInstance.isLogin(MainApplication.getInstance()) && LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo() != null) {
            currentUserId = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo().getUid();
        }
    }

    public String getPersonalDesc() {
        return sp.getString(PERSONAL_DESC, DEFAULT_STRING);
    }

    public void setPersonalDesc(String text) {
        sp.edit().putString(PERSONAL_DESC, text).commit();
    }

    public static String sPaypalAccount;

    public static void setPaypalAccount(String account) {
        SharedPreferences sp = getInstance().sp;
        sp.edit().putString(KEY_PAYPAL_ACCOUNT_ID, account).commit();
    }

    public static String getPaypalAccount() {
        if (sPaypalAccount == null) {
            synchronized (UserConfig.class) {
                if (sPaypalAccount == null) {
                    SharedPreferences sp = getInstance().sp;
                    sPaypalAccount = sp.getString(KEY_PAYPAL_ACCOUNT_ID, "");
                }
            }
        }
        return sPaypalAccount;
    }

    public static String sMoneyType;

    public static void setSettingMoneyType(String type) {
        sMoneyType = type.toLowerCase();
        SharedPreferences sp = getInstance().sp;
        sp.edit().putString(KEY_UNITINFO_ID, sMoneyType).commit();
    }

    /**
     * 仅仅为 getServiceDetail服务。。。。和 账单
     *
     * @return
     */
    public static String getSettingMoneyType() {
        if (sMoneyType == null) {
            synchronized (UserConfig.class) {
                if (sMoneyType == null) {
                    SharedPreferences sp = getInstance().sp;
                    sMoneyType = sp.getString(KEY_UNITINFO_ID, UnitUtils.DEFAULT_MONEY_TYPE).toLowerCase();
                }
            }
        }
        return sMoneyType;
    }

}
