package com.lab.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.cuitrip.model.SavedLocalService;
import com.cuitrip.model.ServiceInfo;

/**
 * Created on 7/27.
 */
public class SavedDescSharedPreferences {

    private static final String FILE_NAME = "desc_pref";
    private static final String SERVICE_INFO = "service_info";

    public static void saveServiceDesc(Context content, ServiceInfo serviceInfo) {
        SharedPreferences.Editor editor = content.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        LogHelper.e("omg","set "+JSONObject.toJSONString(serviceInfo));
        editor.putString(SERVICE_INFO, JSONObject.toJSONString(serviceInfo)).commit();
    }

    public static void deleteServiceDesc(Context content) {
        SharedPreferences.Editor editor = content.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.remove(SERVICE_INFO);
        editor.commit();
    }

    public static SavedLocalService getSavedServiceDesc(Context content) {
        LogHelper.e("omg","get saved ");
        SharedPreferences sharedPreferences = content.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        String title = sharedPreferences.getString(SERVICE_INFO, "");
        LogHelper.e("omg","get  "+title);
        if(TextUtils.isEmpty(title) ){
            return null;
        }else{
            SavedLocalService  savedService = null;
            try {
                savedService = (SavedLocalService) JSONObject.parseObject(title, SavedLocalService.class);
            } catch (Exception e) {
                LogHelper.e("omg","get  exception"+e.getMessage());
                return null;
            }
            return savedService;
        }
    }
}
