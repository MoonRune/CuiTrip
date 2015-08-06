package com.cuitrip.app;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.cuitrip.login.LoginInstance;
import com.cuitrip.service.R;
import com.lab.app.BaseAppLication;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.SmsSdkHelper;

import io.rong.imkit.RongContext;
import io.rong.imlib.RongIMClient;


public class MainApplication extends BaseAppLication {
    public static final boolean IS_DEV = true;
    public static MainApplication sContext;

    private int mPageWidth;
    private int mPageHeight;

    public static MainApplication getInstance() {
        return sContext;
    }
    public void onCreate() {
        super.onCreate();
        sContext = this;
        ImageHelper.initImageLoader(getApplicationContext());
        SmsSdkHelper.initSmsSDK(getApplicationContext());
        RongIMClient.init(this);
        RongContext.init(this);
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

    public void logOut(){
        MessageUtils.showToast(getString(R.string.please_relogin));
        LoginInstance.logout(this);
        Intent intent = new Intent(this, LogoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
