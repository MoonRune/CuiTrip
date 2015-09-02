package com.cuitrip.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.cuitrip.business.UserBusiness;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.app.BrowserActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.SmsSdkHelper;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.CountryPage;

public class RegisterActivity extends BaseActivity implements View.OnClickListener,
        Handler.Callback, CountryPage.OnResult {

    private static final String TAG = "RegisterActivity";
    private static final int UPDATE_TIME = 1;
    private static final int DURATION = 30;

    public static final String FIND_PASSWD = "find_password";

    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";
    private HashMap<String, String> countryRules;

    private TextView mPassWd;
    private TextView mCountry;
    private TextView mGetcode;
    private EditText mPhoneNumber;
    private TextView mNick;
    private TextView mVcode;

    private Handler mHandler;

    private String currentId;
    private String currentCode;

    private boolean mFindPasswd;

    private AsyncHttpClient mClient = new AsyncHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra(FIND_PASSWD, false)) {
            mFindPasswd = true;
        } else {
            mFindPasswd = false;
        }
        setContentView(R.layout.ct_activity_register);
        SmsSdkHelper.registerEventHandler(mEventHandler);

        mPassWd = (TextView) findViewById(R.id.ct_passwd);
        mCountry = (TextView) findViewById(R.id.ct_contry);
        mPhoneNumber = (EditText) findViewById(R.id.ct_mobile);
        mNick = (TextView) findViewById(R.id.ct_nick);
        mVcode = (TextView) findViewById(R.id.ct_vcode);
        mNick.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mNick.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (valid()) {
                    if (mFindPasswd) {
                        submitForForget();
                    } else {
                        submit();
                    }
                }
                return true;
            }
        });
        mGetcode = (TextView) findViewById(R.id.ct_get_vcode);
        mGetcode.setOnClickListener(this);
        findViewById(R.id.nick_layout).setVisibility(mFindPasswd ? View.VISIBLE : View.GONE);
        if (mFindPasswd) {
            mNick.setHint(R.string.ct_login_input_pw_again);
            mNick.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            findViewById(R.id.ct_register_agreement).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.ct_regist)).setText(R.string.ct_login_find_now);
        } else {
            findViewById(R.id.ct_register_mid).setOnClickListener(this);
        }
        findViewById(R.id.back_press).setOnClickListener(this);
        findViewById(R.id.ct_regist).setOnClickListener(this);
        findViewById(R.id.ct_contry).setOnClickListener(this);
        findViewById(R.id.login_in).setOnClickListener(this);
        findViewById(R.id.ct_account_clear).setOnClickListener(this);
        findViewById(R.id.ct_passwd_clear).setOnClickListener(this);
        findViewById(R.id.ct_nick_clear).setOnClickListener(this);
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

        mNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onNickChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mHandler = new Handler(this);
        String[] country = getCurrentCountry();
        if (country != null) {
            currentCode = country[1];
            setCurrentCode(currentCode);
        }
    }

    public void setCurrentCode(String currentCode){

        if (currentCode!=null &&currentCode.equals("886")){
            if(TextUtils.isEmpty(mPhoneNumber.getText().toString().trim())) {
                mPhoneNumber.setText("0");
                mPhoneNumber.setSelection(mPhoneNumber.getText().toString().length());
            }
        }
        mCountry.setText( "+" + currentCode);
    }

    public void onAccountChanged() {
        findViewById(R.id.ct_account_clear).setVisibility(TextUtils.isEmpty(mPhoneNumber.getText().toString()) ? View.INVISIBLE : View.VISIBLE);
    }

    public void onPasswordChanged() {
        findViewById(R.id.ct_passwd_clear).setVisibility(TextUtils.isEmpty(mPassWd.getText().toString()) ? View.INVISIBLE : View.VISIBLE);
    }

    public void onNickChanged() {
        findViewById(R.id.ct_nick_clear).setVisibility(TextUtils.isEmpty(mNick.getText().toString()) ? View.INVISIBLE : View.VISIBLE);
    }

    protected void onDestroy() {
        super.onDestroy();
        SmsSdkHelper.unregisterEventHandler(mEventHandler);
        mClient.cancelAllRequests(true);
    }


    private String[] getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
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
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ct_regist:
                if (valid()) {
                    if (mFindPasswd) {
                        submitForForget();
                    } else {
                        submit();
                    }
                }
                break;

            case R.id.ct_account_clear:
                mPhoneNumber.setText("");
                break;
            case R.id.ct_passwd_clear:
                mPassWd.setText("");
                break;
            case R.id.ct_nick_clear:
                mNick.setText("");
                break;
            case R.id.ct_register_mid:
                startActivity(new Intent(this, BrowserActivity.class)
                        .putExtra(BrowserActivity.DATA, "file:///android_asset/html_about.html")
                        .putExtra(BrowserActivity.TITLE, getString(R.string.ct_gongyue)));
                break;
            case R.id.ct_get_vcode:
                if (TextUtils.isEmpty(mPhoneNumber.getText().toString().trim())) {
                    MessageUtils.showToast(R.string.ct_null_phone);
                    return;
                }
                if (TextUtils.isEmpty(currentCode)) {
                    MessageUtils.showToast(R.string.ct_null_country);
                    return;
                }
                SmsSdkHelper.getVerificationCode(currentCode, mPhoneNumber.getText().toString().trim());
                mGetcode.setText(R.string.ct_geting_vcode);
                mGetcode.setClickable(false);
                break;
            case R.id.ct_contry:
                // 国家列表
                CountryPage countryPage = new CountryPage();
                countryPage.setCountryId(currentId);
                countryPage.setCountryRuls(countryRules);
                countryPage.setOnResultListener(this);
                countryPage.showForResult(this, null);
                break;
            case R.id.login_in:
                finish();
                break;
            case R.id.back_press:
                finish();
                break;
        }
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
        if (TextUtils.isEmpty(mVcode.getText())) {
            MessageUtils.showToast(R.string.ct_null_vcode);
            return false;
        }
        if (TextUtils.isEmpty(mPassWd.getText())) {
            MessageUtils.showToast(R.string.ct_null_passwd);
            return false;
        }
        if (mNick.isShown() && TextUtils.isEmpty(mNick.getText())) {
            if (mFindPasswd) {
                MessageUtils.showToast(R.string.ct_login_input_pw_again);
            } else {
                MessageUtils.showToast(R.string.ct_null_nick);
            }
            return false;
        }
        return true;
    }

    private void submit() {
        showLoading();
        UserBusiness.register(this, mClient, new LabAsyncHttpResponseHandler(UserInfo.class) {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        hideLoading();
                        if (data != null) {
                            UserInfo info = (UserInfo) data;
                            LoginInstance.update(RegisterActivity.this, info);
                            MessageUtils.showToast(R.string.ct_registerd_suc);
                        } else {
                            MessageUtils.showToast(R.string.ct_registerd_suc_login);
                            LoginInstance.logout(RegisterActivity.this);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        hideLoading();
                        if (response != null && !TextUtils.isEmpty(response.msg)) {
                            MessageUtils.showToast(response.msg);
                        }
                    }
                }, mPhoneNumber.getText().toString(), currentCode, mPassWd.getText().toString(),
                mVcode.getText().toString(), mNick.getText().toString());
    }

    private void submitForForget() {
        showLoading();
        UserBusiness.resetPassword(this, mClient, new LabAsyncHttpResponseHandler(UserInfo.class) {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        hideLoading();
                        if (data != null) {
                            UserInfo info = (UserInfo) data;
                            LoginInstance.update(RegisterActivity.this, info);
                            MessageUtils.showToast(R.string.ct_find_suc);
                        } else {
                            MessageUtils.showToast(R.string.ct_find_suc_login);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        hideLoading();
                        if (response != null && !TextUtils.isEmpty(response.msg)) {
                            MessageUtils.showToast(response.msg);
                        }
                    }
                }, mPhoneNumber.getText().toString(), currentCode, mPassWd.getText().toString(),
                mVcode.getText().toString(), mNick.getText().toString());
    }

    EventHandler mEventHandler = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                LogHelper.d(TAG, "afterEvent success");
                //回调完成
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MessageUtils.showToast(R.string.ct_register_vcode_suc);
                        }
                    });
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(UPDATE_TIME, DURATION), 1000);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mGetcode.setText(DURATION + "s");
                            mGetcode.setClickable(false);
                        }
                    });
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    onCountryListGot((ArrayList<HashMap<String, Object>>) data);
                }
            } else {
                LogHelper.d(TAG, "afterEvent error");
                try {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mGetcode.setText(getText(R.string.ct_get_vcode));
                            mGetcode.setClickable(true);
                        }
                    });
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;

                    JSONObject object = new JSONObject(
                            throwable.getMessage());
                    final String des = object.optString("detail");
                    if (!TextUtils.isEmpty(des)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MessageUtils.showToast(des);
                            }
                        });
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MessageUtils.showToast(R.string.network_data_error);
                    }
                });
            }
        }

        public void onRegister() {
            LogHelper.d(TAG, "onRegister success");
        }

        public void beforeEvent(int var1, Object var2) {
        }

        public void onUnregister() {
            LogHelper.d(TAG, "onUnregister success");
        }
    };

    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        // 解析国家列表
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }

            if (countryRules == null) {
                countryRules = new HashMap<String, String>();
            }
            countryRules.put(code, rule);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case UPDATE_TIME:
                try {
                    int time = (Integer) message.obj;
                    if (time > 0) {
                        mGetcode.setText(time + "s");
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(UPDATE_TIME, --time), 1000);
                    } else {
                        mGetcode.setText(getText(R.string.ct_get_vcode));
                        mGetcode.setClickable(true);
                    }
                } catch (Exception e) {
                    mGetcode.setText(getText(R.string.ct_get_vcode));
                    mGetcode.setClickable(true);
                }
                return true;
        }
        return false;
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
                    setCurrentCode(currentCode);
                }
                checkPhoneNum(mPhoneNumber.getText().toString().trim(), currentCode);
            }
        }
    }
}
