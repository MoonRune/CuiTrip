package com.lab.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created on 7/16.
 */
public class Utils {

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_MOTH = "MM-dd";

    public static final String DATE_FORMAT_SECOND_M = "yyyy-MM-dd HH:mm:ss.sss";
    public static final String DATE_FORMAT_DAY_Z = "yyyy-MM-dd z";


    public static String getAppVersionName(Application application) {
        try {
            PackageManager pm = application.getPackageManager();
            if (pm != null) {
                PackageInfo pi = pm.getPackageInfo(application.getPackageName(), 0);
                String versionName = pi.versionName;
                if (versionName != null && versionName.length() > 0) {
                    return versionName;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return "";
    }

    public static String parseLongTimeToString(long time, String format) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+800"));

        String result=sdf.format(date);
        return result ;
    }

    public static long parseStringToLongTime(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+800"));
        try {
            long result = sdf.parse(time.trim()).getTime();
            return result;
        } catch (Exception e) {
            return -1;
        }
    }


    static SimpleDateFormat H_M = new SimpleDateFormat("HH:mm", Locale.CHINA);
    static SimpleDateFormat Y_M_D_H_M = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    public static String getDateFormat(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(dateTime);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        int year1 = calendar.get(Calendar.YEAR);

        if (year == year1) {
            if (day == day1) {
                return H_M.format(dateTime);
            } else if (day - day1 == 1) {
                return MainApplication.sContext.getString(R.string.ct_yestoday) + H_M.format(dateTime);
            } else {
                return Y_M_D_H_M.format(dateTime);
            }
        } else {
            return Y_M_D_H_M.format(dateTime);
        }
    }

    public static String getDateFormat(String dateTime, String format) {
        return getDateFormat(parseStringToLongTime(dateTime, format));
    }

    public static void requestDial(Activity activity, String tele) {
        if (!TextUtils.isEmpty(tele)) {
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + tele)));
            } catch (Exception e) {
                try {
                    AlertDialog.Builder builder = MessageUtils.createHoloBuilder(activity);
                    builder.setMessage(activity.getText(R.string.ct_call_phone_fail) + tele)
                            .create().show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            MessageUtils.showToast(activity.getString(R.string.ct_null_phone_num));
        }
    }
}
