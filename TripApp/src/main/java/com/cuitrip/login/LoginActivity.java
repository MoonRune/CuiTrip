package com.cuitrip.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.cuitrip.app.IndexActivity;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.umeng.message.UmengRegistrar;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.SMSSDK;
import cn.smssdk.gui.CountryPage;

public class LoginActivity extends BaseActivity implements View.OnClickListener, CountryPage.OnResult {
    private static final String TAG = "LoginActivity";

    private static final int GO_REGISTEER = 100;

    private TextView mPassWd;
    private TextView mCountry;
    private TextView mPhoneNumber;

    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";
    private HashMap<String, String> countryRules;
    private String currentId;
    private String currentCode;

    private AsyncHttpClient mClient = new AsyncHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_login);

        setContentView(R.layout.ct_activity_login);
        mPassWd = (TextView) findViewById(R.id.ct_passwd);
        mCountry = (TextView) findViewById(R.id.ct_contry);
        mPhoneNumber = (TextView) findViewById(R.id.ct_mobile);
        mPassWd.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mPassWd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (valid()) {
                    submit();
                }
                return true;
            }
        });
        findViewById(R.id.ct_account_clear).setOnClickListener(this);
        findViewById(R.id.ct_passwd_clear).setOnClickListener(this);
        findViewById(R.id.back_press).setOnClickListener(this);
        findViewById(R.id.find_password).setOnClickListener(this);
        findViewById(R.id.ct_login).setOnClickListener(this);
        findViewById(R.id.ct_go_regist).setOnClickListener(this);
        findViewById(R.id.ct_contry).setOnClickListener(this);

        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onAccountChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPassWd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onPasswordChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String[] country = getCurrentCountry();
        if (country != null) {
            currentCode = country[1];
            mCountry.setText( "+" + currentCode);
        }
    }


    public void onAccountChanged(){
        findViewById(R.id.ct_account_clear).setVisibility(TextUtils.isEmpty(mPhoneNumber.getText().toString())?View.INVISIBLE:View.VISIBLE);
    }

    public void onPasswordChanged(){
        findViewById(R.id.ct_passwd_clear).setVisibility(TextUtils.isEmpty(mPassWd.getText().toString())?View.INVISIBLE:View.VISIBLE);
    }
    protected void onDestroy() {
        super.onDestroy();
        //mClient.cancelAllRequests(true);
    }

    public void finPassword() {
        startActivityForResult(new Intent(this, RegisterActivity.class)
                .putExtra(RegisterActivity.FIND_PASSWD, true), GO_REGISTEER);
    }

    private String[] getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
            if (country != null) {
                LogHelper.e("omg", TextUtils.join("|", country));
            }
        }

        if (country == null) {
            LogHelper.w("SMSSDK", "no country found by MCC: " + mcc);
            country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        }
        return country;
    }

    private String getMCC() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String temp = tm.getSimOperator();
        if (!TextUtils.isEmpty(temp)) {
            if (temp.length() > 3) {
                return temp.substring(0, 3);
            }
            return temp;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        temp = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(temp)) {
            if (temp.length() > 3) {
                return temp.substring(0, 3);
            }
            return temp;
        }
        return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ct_login:
                if (valid()) {
                    submit();
                }
                break;
            case R.id.find_password:
                finPassword();
                break;
            case R.id.ct_account_clear:
                mPhoneNumber.setText("");
                break;
            case R.id.ct_passwd_clear:
                mPassWd.setText("");
                break;
            case R.id.back_press:
                finish();
                break;
            case R.id.ct_contry:
                // 国家列表
                CountryPage countryPage = new CountryPage();
                countryPage.setCountryId(currentId);
                countryPage.setCountryRuls(countryRules);
                countryPage.setOnResultListener(this);
                countryPage.showForResult(this, null);
                break;
            case R.id.ct_go_regist:
                startActivityForResult(new Intent(this, RegisterActivity.class), GO_REGISTEER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GO_REGISTEER && resultCode == RESULT_OK
                && LoginInstance.isLogin(this)) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean valid() {
        if (TextUtils.isEmpty(mCountry.getText())) {
            MessageUtils.showToast(R.string.ct_null_country);
            return false;
        }
        if (TextUtils.isEmpty(mPhoneNumber.getText())) {
            MessageUtils.showToast(R.string.ct_null_phone);
            return false;
        }
        if (TextUtils.isEmpty(mPassWd.getText())) {
            MessageUtils.showToast(R.string.ct_null_passwd);
            return false;
        }
        return true;
    }

    private void submit() {
        showLoading();
        UserBusiness.login(this, mClient, new LabAsyncHttpResponseHandler(UserInfo.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideLoading();
                if (data != null) {
                    MessageUtils.showToast(R.string.ct_login_suc);
                    setResult(RESULT_OK);
                    String deviceId = UmengRegistrar.getRegistrationId(LoginActivity.this);
                    LogHelper.e("LoginActivity", "device_id: " + deviceId);
                    UserInfo info = (UserInfo) data;
                    if (!TextUtils.isEmpty(deviceId)) {
                        UserBusiness.upDevicetoken(LoginActivity.this, mClient, new LabAsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(LabResponse response, Object data) {
                                LogHelper.e("LoginActivity", "device_id: suc");
                            }

                            @Override
                            public void onFailure(LabResponse response, Object data) {
                                LogHelper.e("LoginActivity", "device_id: failed ");
                            }
                        }, deviceId, ((UserInfo) data).getUid(), ((UserInfo) data).getToken());
                    }
                    //TODO 发现者模式下登录切换到旅行者？
                    info.setType(UserInfo.USER_TRAVEL);

                            UserInfo oldInfo = LoginInstance.getInstance(LoginActivity.this).getUserInfo();
                    if (oldInfo == null || (oldInfo != null && !oldInfo.isTravel())) {
                        LoginInstance.update(LoginActivity.this, info);
                        LogHelper.e("LoginActivity", "jump index");
                        Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        LoginInstance.update(LoginActivity.this, info);
                    }


                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideLoading();
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                }
            }
        }, mPhoneNumber.getText().toString(), currentCode, mPassWd.getText().toString());
    }

    @Override
    public void onResult(HashMap<String, Object> data) {
        if (data != null) {
            int page = (Integer) data.get("page");
            if (page == 1) {
                // 国家列表返回
                currentId = (String) data.get("id");
                countryRules = (HashMap<String, String>) data.get("rules");
                String[] country = SMSSDK.getCountry(currentId);
                if (country != null) {
                    currentCode = country[1];
                    mCountry.setText("+" + currentCode);
                }
                checkPhoneNum(mPhoneNumber.getText().toString().trim(), currentCode);
            }
        }
    }

    /**
     * 检查电话号码
     */
    private void checkPhoneNum(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if (TextUtils.isEmpty(phone)) {
            //MessageUtils.showToast(R.string.ct_null_phone);
            return;
        }

        String rule = countryRules.get(code);
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);
        if (!m.matches()) {
            MessageUtils.showToast(R.string.ct_error_phone);
            return;
        }
    }
}

