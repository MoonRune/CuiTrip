package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;


/**
 * Created on 7/20.
 */
public class CancelOrderActivity extends BaseActivity implements View.OnClickListener {

    private OrderItem mOrderInfo;
    private AsyncHttpClient mClient = new AsyncHttpClient();
    private TextView mContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mOrderInfo = (OrderItem) intent.getSerializableExtra(OrderDetailActivity.ORDER_INFO);
        if (mOrderInfo == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(R.string.ct_order_cancel);
        setContentView(R.layout.ct_order_cancel);
        setViewText(R.id.service_name, mOrderInfo.getServiceName());
        UserInfo userInfo = LoginInstance.getInstance(this).getUserInfo();
        if (userInfo.isTravel()) {
            setViewText(R.id.cuthor_name, mOrderInfo.getInsiderName());
        } else {
            setViewText(R.id.cuthor_name, mOrderInfo.getTravellerName());
            TextView cancelTips = (TextView) findViewById(R.id.cancel_tips);
            cancelTips.setText(R.string.ct_cancel_order_tips);
        }
        setViewText(R.id.service_address, mOrderInfo.getServiceAddress());
        ImageHelper.displayPersonImage(mOrderInfo.getHeadPic(), (ImageView) findViewById(R.id.author_img), null);
        findViewById(R.id.order_contact).setOnClickListener(this);
        findViewById(R.id.cancel_order).setOnClickListener(this);
        mContent = (TextView) findViewById(R.id.content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_order:
                if (TextUtils.isEmpty(mContent.getText())) {
                    MessageUtils.showToast(R.string.ct_cancel_null_reason);
                    return;
                }
                MessageUtils.dialogBuilder(CancelOrderActivity.this, true, null, "确定要取消订单吗", null, getString(R.string.ct_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder();
                    }
                });
                break;
            case R.id.order_contact:
                startActivity(new Intent(this, MessageDetailActivity.class)
                        .putExtra(OrderDetailActivity.ORDER_ID, mOrderInfo.getOid()));
                break;
        }
    }

    private void cancelOrder() {
        showNoCancelDialog();
        LogHelper.e("cancel order", "" + mOrderInfo.getOid());
        OrderBusiness.cancelOrder(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideNoCancelDialog();
                startActivity(new Intent(CancelOrderActivity.this, CancelOrderSuccessActivity.class)
                        .putExtra(OrderDetailActivity.ORDER_INFO, mOrderInfo));
                LocalBroadcastManager.getInstance(CancelOrderActivity.this).sendBroadcast(
                        new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideNoCancelDialog();
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                }
                return;
            }
        }, mOrderInfo.getOid(), mContent.getText().toString());
    }

}
