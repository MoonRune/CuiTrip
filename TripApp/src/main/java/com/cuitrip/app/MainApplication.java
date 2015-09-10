package com.cuitrip.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.cuitrip.app.map.GaoDeMapActivity;
import com.cuitrip.app.rong.RongCloudEvent;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.cuitrip.util.IResourceFetcher;
import com.cuitrip.util.PlatformUtil;
import com.lab.app.BaseAppLication;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.SmsSdkHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.umeng.analytics.MobclickAgent;

import io.rong.imkit.RongIM;
import jonathanfinerty.once.Once;


public class MainApplication extends BaseAppLication {
    public static final String DAILY_FORCE_UPDATE = "MainApplication.DAILY_FORCE_UPDATE";
    public static final boolean IS_DEV = false;
    private static MainApplication sContext;

    private int mPageWidth;
    private int mPageHeight;
    private String versionName;

    public static MainApplication getInstance() {
        return sContext;
    }

    public void initRongIM() {
        RongIM.init(this);
    }

    public String getVersionName() {
        if (TextUtils.isEmpty(versionName)) {
            getAppInfo();
        }
        return versionName;
    }

    private void getAppInfo() {
        try {
            String pkName = this.getPackageName();
            versionName = this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (Exception e) {
        }
    }

    public void initRongImCallback() {
        RongIM.getInstance().getRongIMClient().setOnReceivePushMessageListener(RongCloudEvent.getInstance());
        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(RongCloudEvent.getInstance());
        RongIM.setConversationBehaviorListener(RongCloudEvent.getInstance());
        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(RongCloudEvent.getInstance());
        RongIM.getInstance().setSendMessageListener(RongCloudEvent.getInstance());
        RongIM.setUserInfoProvider(RongCloudEvent.getInstance(), true);
        RongIM.setLocationProvider(new RongIM.LocationProvider() {
            @Override
            public void onStartLocation(final Context context, final LocationCallback callback) {
//保存调用过来的回调接口在应用上下文。
                MainApplication.getInstance().setCallback(callback);
//开启定位页面。
                GaoDeMapActivity.returnForIM(context);
            }
        });

    }

    public static RongIM.LocationProvider.LocationCallback callback;

    public static RongIM.LocationProvider.LocationCallback getCallback() {
        return callback;
    }

    public static void setCallback(RongIM.LocationProvider.LocationCallback callback) {
        MainApplication.callback = callback;
    }

    public void initStictMode() {
        if (IS_DEV) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .penaltyDialog()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
        }
    }

    public void onCreate() {
        super.onCreate();
        sContext = this;
        Once.initialise(this);
        initRongIM();
        ImageHelper.initImageLoader(getApplicationContext());
        SmsSdkHelper.initSmsSDK(getApplicationContext());
        if ("com.cuitrip.service".equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            initRongIM();
            /**
             * 融云SDK事件监听处理
             *
             * 注册相关代码，只需要在主进程里做。
             */
            if ("com.cuitrip.service".equals(getCurProcessName(getApplicationContext()))) {
                initRongImCallback();
//                RongCloudEvent.init(this);
//                DemoContext.init(this);
//                Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));
//                try {
//                    RongIM.registerMessageType(DeAgreedFriendRequestMessage.class);
//                    RongIM.registerMessageTemplate(new DeContactNotificationMessageProvider());
////                RongIM.registerMessageTemplate(new DeAgreedFriendRequestMessageProvider());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        init();
        initStictMode();
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
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

        //init platform about  like resourcefetchet
        PlatformUtil.getInstance().setmResourceFetcher(new IResourceFetcher() {
            @Override
            public String getString(int id) {
                return MainApplication.getInstance().getString(id);
            }

            @Override
            public String getString(int id, Object... objs) {
                return MainApplication.getInstance().getString(id, objs);
            }

            @Override
            public int getColor(int id) {
                return MainApplication.getInstance().getResources().getColor(id);
            }
        });
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

    public void logOutWithError() {
        MessageUtils.showToast(getString(R.string.please_relogin));
        logOut();
    }

    public boolean validateRong(){
        if (!LoginInstance.isLogin(this)) {
            LogHelper.e("ron","disconnect");
            RongCloudEvent.DisConnectRong();
            return false;
        }
        return true;
    }
    public void logOut() {
        cleanDeviceToken();
        LoginInstance.logout(this);
        Intent intent = new Intent(this, LogoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    AsyncHttpClient mClient = new AsyncHttpClient();

    /**
     * String deviceId = UmengRegistrar.getRegistrationId(this);
     *
     *  should  build new api  post only  deviceId ，then server delete this id
     */
    public void cleanDeviceToken() {
        LogHelper.e("LoginActivity", "device_id: clean");
        UserInfo info = (UserInfo) LoginInstance.getInstance(this).getUserInfo();
        UserBusiness.upDevicetoken(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                LogHelper.e("LoginActivity", "device_id: suc");
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                LogHelper.e("LoginActivity", "device_id: failed ");
            }
        }, "deletetokenwhycannotpassemptyparam", info.getUid(), info.getToken());
    }

    public void orderMemberIdError() {
        MobclickAgent.onEventEnd(this, "1001");
    }

    public void orderRongMembersizeError() {
        MobclickAgent.onEventEnd(this, "1002");
    }
}
