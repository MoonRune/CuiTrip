package com.cuitrip.app.base;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.cuitrip.app.UserConfig;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

import java.util.HashMap;

/**
 * Created by baziii on 15/8/17.
 */
public class UnitUtils {

    public static final int IMAGE_CROP_WIDTH=1;
    public static final int IMAGE_CROP_HEIGHT=1;
    public static final String DEFAULT_MONEY_TYPE = "cny";
    public static final String DEFAULT_CASH_MONEY_TYPE = "usd";
    public static String[] MONEY_TYPES = {DEFAULT_MONEY_TYPE.toUpperCase(), "twd".toUpperCase(), DEFAULT_CASH_MONEY_TYPE.toUpperCase()};

    public static String getDefaultCity() {
        return "";
    }

    public static void setSettingMoneyType(String type) {
        UserConfig.setSettingMoneyType(type);
    }

    /**
     * 仅仅为 getServiceDetail服务。。。。和 账单
     *
     * @return
     */
    public static String getSettingMoneyType() {
        return UserConfig.getSettingMoneyType();
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
