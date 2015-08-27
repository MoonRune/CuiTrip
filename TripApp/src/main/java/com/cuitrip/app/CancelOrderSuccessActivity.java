package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.MessageUtils;

/**
 * Created on 7/22.
 */
public class CancelOrderSuccessActivity extends BaseActivity  {
    public static final String ORDER_INFO = "ORDER_INFO";
    private OrderItem mOrderInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mOrderInfo = (OrderItem) intent.getSerializableExtra(CancelOrderSuccessActivity.ORDER_INFO);
        if (mOrderInfo == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(R.string.ct_cancel_suc);
        setContentView(R.layout.ct_order_cancel_sux);
        setViewText(R.id.service_name, mOrderInfo.getServiceName());
        setViewText(R.id.service_address, mOrderInfo.getServiceAddress());
        findViewById(R.id.go_order_list).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(CancelOrderSuccessActivity.this, IndexActivity.class)
                        .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.ORDER_TAB));
                finish();
            }
        });
    }
}
