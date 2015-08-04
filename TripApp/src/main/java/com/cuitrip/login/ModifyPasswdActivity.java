package com.cuitrip.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cuitrip.business.UserBusiness;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created on 7/16.
 */
public class ModifyPasswdActivity extends BaseActivity {
    private TextView mOnce;
    private TextView mTwice;
    private AsyncHttpClient mClient = new AsyncHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_modify_pass);
        setContentView(R.layout.ct_modify_password);
        mOnce = (TextView) findViewById(R.id.ct_passwd_one);
        mTwice = (TextView) findViewById(R.id.ct_passwd_twice);
        mTwice.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mTwice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                tryModify();
                return true;
            }
        });
        ((CheckBox) findViewById(R.id.toggle_pw)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mOnce.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mTwice.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mOnce.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mTwice.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.cancelAllRequests(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_OK:
                tryModify();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void tryModify() {
        if (TextUtils.isEmpty(mOnce.getText()) || TextUtils.isEmpty(mTwice.getText())) {
            MessageUtils.showToast(R.string.ct_null_passwd);
            return;
        }
        showNoCancelDialog();
        UserBusiness.modifyPassword(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideNoCancelDialog();
                MessageUtils.showToast(R.string.ct_modify_suc);
                startActivity(new Intent(ModifyPasswdActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideNoCancelDialog();
                MessageUtils.showToast(response.msg);

            }
        }, mOnce.getText().toString(), mTwice.getText().toString());
    }
}
