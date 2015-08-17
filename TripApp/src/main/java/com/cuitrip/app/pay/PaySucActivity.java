package com.cuitrip.app.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/17.
 */
public class PaySucActivity extends BaseActivity {
    public static final String EMAIL_KEY = "EMAIL_KEY";
    @InjectView(R.id.email_tv)
    TextView emailTv;
    @InjectView(R.id.back_tv)
    TextView backTv;

    public static void start(Context context, String email) {
        context.startActivity(new Intent(context, PaySucActivity.class).putExtra(EMAIL_KEY, email));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(getString(R.string.pay_suc));
        setContentView(R.layout.ct_order_pay_suc);
        ButterKnife.inject(this);
        emailTv.setText(getIntent().getStringExtra(EMAIL_KEY));
    }

    @OnClick(R.id.back_tv)
    public void clickBack() {
        finish();
    }
}
