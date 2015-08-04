package com.lab.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cuitrip.model.SavedLocalService;

/**
 * Created on 7/27.
 */
public class SavedDescSharedPreferences {

    private static final String FILE_NAME = "desc_pref";
    private static final String TITLE_KEY = "title_key";
    private static final String DESC_KEY = "desc_key";
    private static final String MAINPIC_KEY = "mainpic_key";

    public static void saveServiceDesc(Context content, String title, String desc, String mainPic) {
        SharedPreferences.Editor editor = content.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(TITLE_KEY, title);
        editor.putString(MAINPIC_KEY, mainPic);
        editor.putString(DESC_KEY, desc).commit();
    }

    public static void deleteServiceDesc(Context content) {
        SharedPreferences.Editor editor = content.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.remove(TITLE_KEY);
        editor.remove(DESC_KEY);
        editor.remove(MAINPIC_KEY);
        editor.commit();
    }

    public static SavedLocalService getSavedServiceDesc(Context content) {
        SharedPreferences sharedPreferences = content.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        String title = sharedPreferences.getString(TITLE_KEY, "");
        String desc = sharedPreferences.getString(DESC_KEY, "");
        if(TextUtils.isEmpty(title) && TextUtils.isEmpty(desc)){
            return null;
        }else{
            SavedLocalService  savedService = new SavedLocalService();
            savedService.title = title;
            savedService.content = desc;
            savedService.mainPic = sharedPreferences.getString(MAINPIC_KEY, "");
            return savedService;
        }
    }
}
