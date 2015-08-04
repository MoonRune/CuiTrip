package com.cuitrip.push;

import android.content.Context;
import android.content.SharedPreferences;

import com.cuitrip.app.MainApplication;

/**
 * Created on 7/21.
 */
public class MessagePrefs {

    private static final String MESSAGE_PREF = "ct_message_pref";
    private static final String MESSAGE_PREF_LAST_TIME_KEY = "ct_last_message_time";
    private static final String MESSAGE_PREF_NEW_KEY = "ct_message_new";
    private static final String MESSAGE_LAST_OPEN_TIME = "ct_message_new";

    public static void saveLastMessageTime(String time){
         SharedPreferences sp = MainApplication.sContext.getSharedPreferences(MESSAGE_PREF,
                 Context.MODE_PRIVATE);
         sp.edit().putString(MESSAGE_PREF_LAST_TIME_KEY, time).commit();
     }

    public static String getLastMessageTime(){
        SharedPreferences sp = MainApplication.sContext.getSharedPreferences(MESSAGE_PREF,
                Context.MODE_PRIVATE);
        return sp.getString(MESSAGE_PREF_LAST_TIME_KEY, null);
    }

    public static void setHasNewMessage(boolean hasNew){
        SharedPreferences sp = MainApplication.sContext.getSharedPreferences(MESSAGE_PREF,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(MESSAGE_PREF_NEW_KEY, hasNew).commit();
    }

    public static boolean hasNewMessage(){
        SharedPreferences sp = MainApplication.sContext.getSharedPreferences(MESSAGE_PREF,
                Context.MODE_PRIVATE);
        return sp.getBoolean(MESSAGE_PREF_NEW_KEY, false);
    }

    public static void setLastDialogReadTime(String orderId, long time){
        SharedPreferences sp = MainApplication.sContext.getSharedPreferences(MESSAGE_PREF,
                Context.MODE_PRIVATE);
        sp.edit().putLong(orderId, time).commit();
    }

    public static boolean isDialogHasNewMessage(String orderId, long lastMessageTime){
        SharedPreferences sp = MainApplication.sContext.getSharedPreferences(MESSAGE_PREF,
                Context.MODE_PRIVATE);
        return lastMessageTime > sp.getLong(orderId, 0);
    }

}
