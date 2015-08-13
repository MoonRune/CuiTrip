package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cuitrip.login.LoginInstance;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.LogHelper;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created on 7/13.
 */
public class LogoActivity extends BaseActivity implements Handler.Callback {

    private static final int DURATION = 3000;
    private static final int GO_MAIN = 100;
    private static final int INIT = 101;

    String token =
//            "iOYYGEULMn9cEFHJvU2KclUJjq/G011LMurM5xSobnWdFPeJcpSCKXQDIt1DpCe4kC4zt6Ilvdl1WC8KOcAiRg=="; //180
    "anTVf0l/BWsxuHHSVIS9QVUJjq/G011LMurM5xSobnWdFPeJcpSCKaeojIan8MAUglmpfkJTWhp1WC8KOcAiRg=="; //179
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_activity_logo);
        Handler handler = new Handler(this);
        handler.sendMessage(handler.obtainMessage(INIT));
        handler.sendMessageDelayed(handler.obtainMessage(GO_MAIN), DURATION);
//        OrderFormActivity.start(this,"");
        RongIM.connect(token, new RongIMClient.ConnectCallback() {

            @Override
            public void onSuccess(String userId) {

//                LogHelper.e( "onResume", "current connect status is:" + RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus());
                LogHelper.e("ron suc", "" + userId);



/* 连接成功 */
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                LogHelper.e("ron failed", "");
/* 连接失败，注意并不需要您做重连 */
            }

            @Override
            public void onTokenIncorrect() {
                LogHelper.e("ron token error", "");
/* Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token */
            }

        });
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
