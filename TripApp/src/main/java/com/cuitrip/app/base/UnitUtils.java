package com.cuitrip.app.base;

import android.support.annotation.NonNull;

import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

import java.util.HashMap;

/**
 * Created by baziii on 15/8/17.
 */
public class UnitUtils {

    public static String getDefaultCity(){
    return "";
    }
    public static String getMoenyType(){
        return "cny";
    }
    //zh-Hant（中文繁体），zh-Hans（中文简体）, en（英语）
    public static String getLanguage() {
        return "zh-Hans";
    }


    //TW，中国： CHN
    public static String getCreateServiceCountry() {
        return "TW";
    }

    //1 身份证 2 护照
    public static String getIndentityName(@NonNull String code) {
        switch (code.trim()) {
            case "1":
                return PlatformUtil.getInstance().getString(R.string.ct_identity_type_idcard);
            case "2":
                return PlatformUtil.getInstance().getString(R.string.ct_identity_type_internation);
            default:
                return "";

        }
    }
    private static HashMap<String,String> sIdentityTypeNameCode =new HashMap<>();

    public static String getIndentityCode(@NonNull String name) {
        if (sIdentityTypeNameCode.isEmpty()){
            sIdentityTypeNameCode.put(PlatformUtil.getInstance().getString(R.string.ct_identity_type_idcard), "1");
            sIdentityTypeNameCode.put(PlatformUtil.getInstance().getString(R.string.ct_identity_type_internation), "2");
        }
        return  sIdentityTypeNameCode.get(name);
    }
}
