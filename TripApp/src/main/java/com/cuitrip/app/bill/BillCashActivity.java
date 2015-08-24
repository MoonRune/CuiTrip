package com.cuitrip.app.bill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/18.
 */
public class BillCashActivity extends BaseActivity {

    public static final String MOENY_KEY = "MOENY_KEY";
    public static final String MOENY_TYPE_KEY = "MOENY_TYPE_KEY";
    public static final String MOENY_RATE_KEY = "MOENY_RATE_KEY";
    @InjectView(R.id.desc)
    TextView desc;
    @InjectView(R.id.amount)
    TextView amount;
    @InjectView(R.id.account)
    EditText account;
    @InjectView(R.id.money)
    TextView money;
    @InjectView(R.id.submit)
    TextView submit;
    String avaliableMoney;
    String avaliableMoneyType;
    String rate;

    AsyncHttpClient mClient = new AsyncHttpClient();

    public static void start(Context context, String money, String moneytype, String rate) {
        context.startActivity(new Intent(context, BillCashActivity.class).putExtra(MOENY_KEY, money)
                .putExtra(MOENY_TYPE_KEY, moneytype).putExtra(MOENY_RATE_KEY, rate));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_get_cash);
        ButterKnife.inject(this);
        showActionBar(getString(R.string.get_cash_title));

        avaliableMoney = getIntent().getStringExtra(MOENY_KEY);
        avaliableMoneyType = getIntent().getStringExtra(MOENY_TYPE_KEY);
        rate = getIntent().getStringExtra(MOENY_RATE_KEY);

        render();
        account.setText(LoginInstance.getPaypalAccount());
    }

    public void render() {
        amount.setText(getString(R.string.currency_money, avaliableMoneyType.toUpperCase(), avaliableMoney));
        try {
            desc.setText(getString(R.string.rate_desc, rate, avaliableMoneyType.toUpperCase()));
        } catch (Exception e) {
            desc.setText(R.string.data_error);
        }
    }

    @OnClick(R.id.submit)
    public void trySubmit() {
        if (TextUtils.isEmpty(account.getText().toString())) {
            MessageUtils.showToast("请输入帐号 ");
            return;
        }
        if (TextUtils.isEmpty(money.getText().toString())) {
            MessageUtils.showToast("请输入金额 ");
            return;
        }
        submit();
    }

    public void submit() {
        showLoading();
        LoginInstance.setPaypalAccount(account.getText().toString());
        UserBusiness.getCash(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                LogHelper.e("omg", "suc " + String.valueOf(response.result));
                MessageUtils.showToast(getString(R.string.get_cash_suc));
                finish();
                hideLoading();
            }

            @Override
            public void onFailure(LabResponse response, Object data) {

                LogHelper.e("omg", "failed ");
                String msg;
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    msg = response.msg;
                } else {
                    msg = PlatformUtil.getInstance().getString(R.string.data_error);
                }
                MessageUtils.showToast(msg);
                hideLoading();
            }
        }, account.getText().toString(), money.getText().toString(), UnitUtils.getCashType());
    }
}
