package com.cuitrip.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.login.ModifyPasswdActivity;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;
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
            findViewById(R.id.ct_money_hint_ll).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.ct_money_value_tv_tv)).setText(UnitUtils.getMoneyType().toUpperCase());


        }else{
            findViewById(R.id.ct_money_hint_ll).setVisibility(View.GONE);
            findViewById(R.id.ct_modify_passwd).setVisibility(View.GONE);
            findViewById(R.id.logout).setVisibility(View.GONE);
        }
        findViewById(R.id.ct_about).setOnClickListener(this);
        findViewById(R.id.ct_money_hint_ll).setOnClickListener(this);
        findViewById(R.id.ct_relation).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.ct_modify_passwd).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ct_money_hint_ll:
              AlertDialog.Builder builder = MessageUtils.createHoloBuilder(this);
                builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
                        UnitUtils.MONEY_TYPES), new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i <  UnitUtils.MONEY_TYPES.length) {
                            UnitUtils.setMoneyType(UnitUtils.MONEY_TYPES[i]);
                            ((TextView) findViewById(R.id.ct_money_value_tv_tv)).setText(UnitUtils.getMoneyType().toUpperCase());
                        }
                    }
                });
                AlertDialog dialog = builder.show();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.height = getResources().getDimensionPixelOffset(R.dimen.ct_dp_240); // 高度
                window.setAttributes(lp);
                break;
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
