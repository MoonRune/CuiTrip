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
import com.cuitrip.util.PlatformUtil;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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

        String result = sdf.format(date);
        return result;
    }

    public static long parseStringToLongTime(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+800"));
        try {
            long result = sdf.parse(time.trim()).getTime();
            LogHelper.e("parseStringToLongTime", time + "|" + result);
            return result;
        } catch (Exception e) {
            return -1;
        }
    }


    static SimpleDateFormat H_M = new SimpleDateFormat("HH:mm", Locale.CHINA);
    static SimpleDateFormat Y_M_D = new SimpleDateFormat(DATE_FORMAT_DAY, Locale.CHINA);
    static SimpleDateFormat Y_M_D_H_M = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    static {
        H_M.setTimeZone(TimeZone.getTimeZone("GMT+800"));
        Y_M_D.setTimeZone(TimeZone.getTimeZone("GMT+800"));
        Y_M_D_H_M.setTimeZone(TimeZone.getTimeZone("GMT+800"));
    }

    public static String getDateFormat(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+800"));
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(dateTime);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        int year1 = calendar.get(Calendar.YEAR);

        if (year == year1) {
            if (day == day1) {
                return H_M.format(dateTime);
            } else if (day - day1 == 1) {
                return MainApplication.getInstance().getString(R.string.ct_yestoday) + H_M.format(dateTime);
            } else {
                return Y_M_D_H_M.format(dateTime);
            }
        } else {
            return Y_M_D_H_M.format(dateTime);
        }
    }

    //
    public static String getMsToD(String dateTime) {

        long value = 0;
        try {
            value = Long.parseLong(dateTime);
        } catch (NumberFormatException e) {
            value = parseStringToLongTime(dateTime, DATE_FORMAT_SECOND);
        }
        String result = Y_M_D.format(value);
        return result;
    }

    public static String getMsToSeconds(String dateTime) {

        long value = 0;
        try {
            value = Long.parseLong(dateTime);
        } catch (NumberFormatException e) {
            value = parseStringToLongTime(dateTime, DATE_FORMAT_SECOND);
        }
        return Y_M_D_H_M.format(value);
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

    public static boolean isAliInstalled() {
        String packageName = "com.eg.android.AlipayGphone";
        final PackageManager packageManager = MainApplication.getInstance().getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public static int dp2pixel(int i) {
        return (int) (0.5F + MainApplication.getInstance().getResources().getDisplayMetrics().density * (float) i);
    }

    public static float dp2pixelF(int i) {
        return (0.5F + MainApplication.getInstance().getResources().getDisplayMetrics().density * (float) i);
    }

    public static String getLocalHostIp() {
        String ipaddress = "127.0.0.1";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ipaddress = ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            return ipaddress;
        }
        return ipaddress;

    }

    public static HashMap<String, String> sGenderHashMap = new HashMap<>();

    public static void initGenderMap() {
        sGenderHashMap.put(PlatformUtil.getInstance().getString(R.string.ct_male_code), PlatformUtil.getInstance().getString(R.string.ct_male));
        sGenderHashMap.put(PlatformUtil.getInstance().getString(R.string.ct_gende_non_code), PlatformUtil.getInstance().getString(R.string.ct_gende_non));
        sGenderHashMap.put(PlatformUtil.getInstance().getString(R.string.ct_female_code), PlatformUtil.getInstance().getString(R.string.ct_female));
    }

    public static String getGender(String code) {
        if (sGenderHashMap == null || sGenderHashMap.isEmpty()) {
            initGenderMap();
        }
        LogHelper.e("omg", "gender" + code + " |" + sGenderHashMap.containsKey(code.trim()));
        if (sGenderHashMap.containsKey(code.trim())) {
            return sGenderHashMap.get(code.trim());
        }
        return code;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

}
