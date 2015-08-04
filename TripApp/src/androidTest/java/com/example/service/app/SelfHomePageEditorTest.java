package com.example.service.app;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.cuitrip.app.SelfHomePageEditorActivity;
import com.lab.utils.Utils;

import junit.framework.Assert;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class SelfHomePageEditorTest extends ActivityInstrumentationTestCase2<SelfHomePageEditorActivity> {
    public SelfHomePageEditorTest() {
        super(SelfHomePageEditorActivity.class);
    }

    SelfHomePageEditorActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testFormate(){
        Assert.assertEquals(1439395200000l, Utils.parseStringToLongTime("2015-08-13", Utils.DATE_FORMAT_DAY));
        Assert.assertEquals("2015-08-13",Utils.parseLongTimeToString(1439395200000l, Utils.DATE_FORMAT_DAY));
    }
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