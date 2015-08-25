package com.example.service.app;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.DisplayMetrics;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.country.CountrySelectActivity;
import com.cuitrip.service.R;
import com.lab.utils.LogHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class CountrySelectTest extends ActivityInstrumentationTestCase2<CountrySelectActivity> {
    public CountrySelectTest() {
        super(CountrySelectActivity.class);
    }

    CountrySelectActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testSplit() {
        Resources resources = MainApplication.getInstance().getResources();
        HashMap<String, String> zh = new HashMap<>();
        HashMap<String, String> tw = new HashMap<>();

        try {
            {
                Configuration config = resources.getConfiguration();//获得设置对象
                DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
                config.locale = Locale.SIMPLIFIED_CHINESE; //简体中
                resources.updateConfiguration(config, dm);
                for (Field field : R.string.class.getFields()) {
                    int value = field.getInt(R.class);
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(resources.getString(value));
                    zh.put(field.getName(), new String(resources.getString(value)));
                }
            }
            {
                Configuration config = resources.getConfiguration();//获得设置对象
                DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
                config.locale = Locale.TRADITIONAL_CHINESE; //繁体
                resources.updateConfiguration(config, dm);
                for (Field field : R.string.class.getFields()) {
                    int value = field.getInt(R.class);
                    tw.put(field.getName(), resources.getString(value));
                }
            }
            LogHelper.e("value", zh.size() + "|" + tw.size() + "------------");
//
            for (Map.Entry<String, String> entry : zh.entrySet()) {

                LogHelper.e("value hehehe", "\"" + entry.getValue() + "\":\"" + tw.get(entry.getKey()) + "\";");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

//    @SmallTest
//    public void testPattern() {
//        String desc = " <div><img src=\"http://cuitrip.oss-cn-shenzhen.aliyuncs.com/179_1438829493471\" width=\"100%\"/></div>";
//
//        Pattern pattern = Pattern.compile(Constants.IMAGE_PATTERN);
//        LogHelper.e("omg","desc :"+desc);
//        LogHelper.e("omg","pattern :"+Constants.IMAGE_PATTERN);
//        Matcher matcher = pattern.matcher(desc);
//        int index = 0;
//        while (matcher.find()) {
//            int start = matcher.start();
//            String temp = desc.substring(index, start);
//            index = matcher.end();
//            LogHelper.e("omg","temp :"+temp);
//        }
//    }

//    @SmallTest
//    public void testFormate(){
//        LogHelper.e("sdf", URLImageParser.badReplae("12342314dsafsdfas.q234fasdf4trfrnwifnoa\n" +
//                "    <div><img src=\"http://cuitrip.oss-cn-shenzhen.aliyuncs.com/180_1438694929621\" width=\"100%\" /></div>"));
//        Assert.assertEquals(1439395200000l, Utils.parseStringToLongTime("2015-08-13", Utils.DATE_FORMAT_DAY));
//        Assert.assertEquals("2015-08-13",Utils.parseLongTimeToString(1439395200000l, Utils.DATE_FORMAT_DAY));
//    }
//    private void testInput(){
//        final String imput="12342314dsafsdfas.q234fasdf4trfrnwifnoa";
//        final Point p= new Point();
//        for (p.x=0;p.x <imput.length();p.x++) {
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    activity.mContentEt.setText(imput.substring(0,p.x));
//
//                }
//            });
//        }
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                activity.testInjectBitmap();
//            }
//        });
//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                activity.trySubmit();
//
//            }
//        });
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}