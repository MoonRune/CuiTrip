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
public class PayFailedActivity extends BaseActivity {
    public static final String ERROR_TEXT = "ERROR_TEXT";
    @InjectView(R.id.back_tv)
    TextView backTv;
    @InjectView(R.id.error_tv)
    TextView errorTv;

    public static void start(Context context, String error) {
        context.startActivity(new Intent(context, PayFailedActivity.class).putExtra(ERROR_TEXT, error));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(getString(R.string.ct_payfailed));
        setContentView(R.layout.ct_order_pay_failed);
        ButterKnife.inject(this);
        errorTv.setText(getIntent().getStringExtra(ERROR_TEXT));
    }

    @OnClick(R.id.back_tv)
    public void clickBack() {
        finish();
    }
}
