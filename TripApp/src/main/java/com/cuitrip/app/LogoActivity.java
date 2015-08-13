package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cuitrip.app.rong.RongCloudEvent;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

/**
 * Created on 7/13.
 */
public class LogoActivity extends BaseActivity implements Handler.Callback {

    private static final int DURATION = 3000;
    private static final int GO_MAIN = 100;
    private static final int INIT = 101;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_activity_logo);
        Handler handler = new Handler(this);
        handler.sendMessage(handler.obtainMessage(INIT));
        handler.sendMessageDelayed(handler.obtainMessage(GO_MAIN), DURATION);
        RongCloudEvent.ConnectRong();
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.what == GO_MAIN) {
            startActivity(new Intent(this, IndexActivity.class));
            finish();
            return true;
        }
        if (message.what == INIT) {
            LoginInstance.getInstance(this); //初始化用户信息
            return true;
        }
        return false;
    }
}
