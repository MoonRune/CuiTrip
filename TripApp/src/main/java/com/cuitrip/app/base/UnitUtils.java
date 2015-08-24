package com.cuitrip.app.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

import java.util.HashMap;

/**
 * Created by baziii on 15/8/17.
 */
public class UnitUtils {
    static final String FILE = "_app_unit_info";


    static final String KEY_UNITINFO_ID = "_user_account_info";

    public static final String DEFAULT_MONEY_TYPE = "cny";
    public static final String DEFAULT_CASH_MONEY_TYPE = "usd";
    public static String[] MONEY_TYPES = {DEFAULT_MONEY_TYPE.toUpperCase(), "twd".toUpperCase(), DEFAULT_CASH_MONEY_TYPE.toUpperCase()};

    public static String getDefaultCity() {
        return "";
    }

    public static String sMoneyType;

    public static void setSettingMoneyType(String type) {
        sMoneyType = type.toLowerCase();
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(FILE, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_UNITINFO_ID, sMoneyType).commit();
    }

    /**
     * 仅仅为 getServiceDetail服务。。。。和 账单
     *
     * @return
     */
    public static String getSettingMoneyType() {
        if (sMoneyType == null) {
            synchronized (UnitUtils.class) {
                if (sMoneyType == null) {
                    SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(FILE, Context.MODE_PRIVATE);
                    sMoneyType = sp.getString(KEY_UNITINFO_ID, DEFAULT_MONEY_TYPE).toLowerCase();
                }
            }
        }
        return sMoneyType;
    }

    //zh-Hant（中文繁体），zh-Hans（中文简体）, en（英语）
    public static String getLanguage() {
        return "zh-Hans";
    }

    public static final String getMoneyType() {
        return DEFAULT_MONEY_TYPE.toLowerCase();
    }

    public static final String getCashType() {
        return DEFAULT_CASH_MONEY_TYPE.toLowerCase();
    }

    //TW，中国： CHN
    public static String getCreateServiceCountry() {
        return "TW";
    }

    //1 身份证 2 护照
    public static String getIndentityName(String code) {
        if (TextUtils.isEmpty(code)) {
            return "";
        }
        switch (code.trim()) {
            case "1":
                return PlatformUtil.getInstance().getString(R.string.ct_identity_type_idcard);
            case "2":
                return PlatformUtil.getInstance().getString(R.string.ct_identity_type_internation);
            default:
                return "";

        }
    }

    private static HashMap<String, String> sIdentityTypeNameCode = new HashMap<>();

    public static String getIndentityCode(@NonNull String name) {
        if (sIdentityTypeNameCode.isEmpty()) {
            sIdentityTypeNameCode.put(PlatformUtil.getInstance().getString(R.string.ct_identity_type_idcard), "1");
            sIdentityTypeNameCode.put(PlatformUtil.getInstance().getString(R.string.ct_identity_type_internation), "2");
        }
        return sIdentityTypeNameCode.get(name);
    }
}
