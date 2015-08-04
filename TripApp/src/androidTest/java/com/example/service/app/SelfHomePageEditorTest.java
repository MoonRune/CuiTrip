package com.example.service.app;

import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.cuitrip.app.SelfHomePageEditorActivity;

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
    public void testSoftInput(){
        try {
            Thread.sleep(3000);
            activity.mContentEt.requestFocus();
            Thread.sleep(4000);
            activity.mContentEt.clearFocus();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testInput(){
        final String imput="12342314dsafsdfas.q234fasdf4trfrnwifnoa西瓜佛那四个脑 is 地方";
        final Point p= new Point();
        for (p.x=0;p.x <imput.length();p.x++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.mContentEt.setText(imput.substring(0,p.x));

                }
            });
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                activity.testInjectBitmap();
            }
        });
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}