package com.example.service.app;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.cuitrip.app.test.TestActivity;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MutiActivityTest extends ActivityInstrumentationTestCase2<TestActivity> {
    public MutiActivityTest() {
        super(TestActivity.class);
    }

    TestActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testSplit() {
        try {
            Thread.sleep(100*1000);
        } catch (InterruptedException e) {
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