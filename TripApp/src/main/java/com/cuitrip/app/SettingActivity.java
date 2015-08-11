package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.login.ModifyPasswdActivity;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created on 7/16.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_setting);
        setContentView(R.layout.ct_setting);
        if(LoginInstance.isLogin(this)){
            findViewById(R.id.ct_modify_passwd).setVisibility(View.VISIBLE);
            findViewById(R.id.logout).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.ct_modify_passwd).setVisibility(View.GONE);
            findViewById(R.id.logout).setVisibility(View.GONE);
        }
        findViewById(R.id.ct_about).setOnClickListener(this);
        findViewById(R.id.ct_relation).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.ct_modify_passwd).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ct_modify_passwd:
                startActivity(new Intent(this, ModifyPasswdActivity.class));
                break;
            case R.id.ct_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.ct_relation:
                startActivity(new Intent(this, RelationActivity.class));
                break;
            case R.id.logout:
                showNoCancelDialog();
                UserBusiness.logout(this, new AsyncHttpClient(), new LabAsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        hideNoCancelDialog();
                        MainApplication.getInstance().logOut();
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        hideNoCancelDialog();
                        MainApplication.getInstance().logOut();
                    }
                });
        }

    }
}
