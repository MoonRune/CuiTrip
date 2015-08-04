package com.lab.app;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.login.LoginActivity;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.push.PushService;
import com.cuitrip.service.R;
import com.lab.utils.AnalyticsHelper;
import com.lab.utils.MessageUtils;
import com.pingplusplus.android.PaymentActivity;
import com.umeng.message.PushAgent;

public class BaseActivity extends AppCompatActivity {
    private static final int REQUEST_LOGIN = 1000;
    public static final int REQUEST_CODE_PAYMENT = 1001;

    public static final String PAY_CHANEL_ALIPAY= "alipay";
    public static final String PAY_CHANEL_WXPAY= "wx";
    private static volatile boolean sAnalyticse = false;

    private Dialog mLoadingDialog;
    private Dialog mNoCancelDialog;

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.ct_black));
        }
        super.onCreate(savedInstanceState);
        if (!sAnalyticse) {
            AnalyticsHelper.updateOnlineConfig(this.getApplicationContext());
            AnalyticsHelper.enableEncrypt(true);
            AnalyticsHelper.setDebugMode(MainApplication.IS_DEV);
            PushAgent mPushAgent = PushAgent.getInstance(this.getApplicationContext());
            mPushAgent.enable();
            mPushAgent.setPushIntentServiceClass(PushService.class);
            sAnalyticse = true;
        }
        PushAgent.getInstance(this).onAppStart();
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setElevation(0);
        }
    }

    public void showActionBar(String title) {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle(title);
            actionbar.show();
        }
    }

    public void showActionBar(int titleId) {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle(getString(titleId));
            actionbar.show();
        }
    }

    protected void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = MessageUtils.getProgressDialog(this, R.string.loading_text);
        }
        mLoadingDialog.show();
    }

    protected void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    protected void showNoCancelDialog(){
        if(mNoCancelDialog == null){
            mNoCancelDialog = MessageUtils.getNoCancelProgressDialog(this, getString(R.string.ct_waiting));
        }
        mNoCancelDialog.show();
    }

    protected void hideNoCancelDialog(){
        if(mNoCancelDialog != null){
            mNoCancelDialog.dismiss();
        }
    }

    protected void reLogin() {
        startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
    }

    public void pay(String data){
        Intent intent = new Intent();
        String packageName = getPackageName();
        ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
        intent.setComponent(componentName);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK && LoginInstance.isLogin(this)) {
                onLoginSuccess();
                return;
            } else {
                onLoginFailed();
            }
        }
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
            /* 处理返回值
             * "success" - payment succeed
             * "fail"    - payment failed
             * "cancel"  - user canceld
             * "invalid" - payment plugin not installed
             *
             * 如果是银联渠道返回 invalid，调用 UPPayAssistEx.installUPPayPlugin(this); 安装银联安全支付控件。
             */
                if(result != null && result.equals("success")){
                    onPaySuccess();
                }else{
                    String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                    onPayFailed(errorMsg);
                }
//                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "User canceled", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onPaySuccess(){

    }

    protected void onPayFailed(String msg){

    }

    protected void onLoginSuccess() {

    }

    protected void onLoginFailed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        setStartAnimation();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        setStartAnimation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setFinishAnimation();
    }

    @Override
    public void finish() {
        super.finish();
        setFinishAnimation();
    }

    protected void setStartAnimation() {
        overridePendingTransition(R.anim.ct_slide_in_right, R.anim.ct_slide_out_left);
    }

    protected void setFinishAnimation() {
        overridePendingTransition(R.anim.ct_slide_in_left, R.anim.ct_slide_out_right);
    }

    protected void onResume() {
        super.onResume();
        AnalyticsHelper.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        AnalyticsHelper.onPause(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        if(mNoCancelDialog != null && mNoCancelDialog.isShowing()){
            mNoCancelDialog.dismiss();
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected void onNetwokError(int nameRes, int drawableRes, int layout) {

    }

    protected void onEmpyData(int nameRes, int drawableRes, int layout) {

    }

    protected void setViewImage(int id, int drawableId) {
        ImageView tempImageView = (ImageView) findViewById(id);
        if (tempImageView != null) {
            tempImageView.setImageResource(drawableId);
        }
    }

    public void setViewText(int id, CharSequence text) {
        TextView v = (TextView) findViewById(id);
        if (v != null) {
            v.setText(text);
        }
    }

    public void removeView(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public void showView(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    protected void hideView(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }
}
