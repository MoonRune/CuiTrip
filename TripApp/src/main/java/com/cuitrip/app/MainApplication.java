package com.cuitrip.app;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.lab.app.BaseAppLication;
import com.lab.utils.ImageHelper;
import com.lab.utils.SmsSdkHelper;


public class MainApplication extends BaseAppLication {
    public static final boolean IS_DEV = false;
    public static MainApplication sContext;

    private int mPageWidth;
    private int mPageHeight;
    public void onCreate() {
        super.onCreate();
        sContext = this;
        ImageHelper.initImageLoader(getApplicationContext());
        SmsSdkHelper.initSmsSDK(getApplicationContext());
        init();
    }

    private void init() {
        DisplayMetrics displaymetrics = new DisplayMetrics();

        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(displaymetrics);
        mPageWidth = displaymetrics.widthPixels;
        mPageHeight = displaymetrics.heightPixels;
        if (mPageWidth > mPageHeight) {
            mPageWidth ^= mPageHeight;
            mPageHeight ^= mPageWidth;
            mPageWidth ^= mPageHeight;
        }
    }
    public int dp2pixel(int i) {
        return (int) (0.5F + getResources().getDisplayMetrics().density * (float) i);
    }

    public int getPageWidth() {
        return mPageWidth;
    }

    public int getPageHeight() {
        return mPageHeight;
    }


}
