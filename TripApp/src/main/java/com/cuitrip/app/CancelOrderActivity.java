package com.cuitrip.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cuitrip.app.pro.ServicePartRenderData;
import com.cuitrip.app.pro.ServicePartWithoutViewHolder;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
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
 * Created on 7/20.
 */
public class CancelOrderActivity extends BaseActivity implements View.OnClickListener {

    public static final String ORDER_KEY = "CancelOrderActivity.ORDER_KEY";
    public static final int REQUEST_CODE = 13;
    @InjectView(R.id.cancel_tips)
    TextView cancelTips;
    @InjectView(R.id.content)
    EditText content;
    @InjectView(R.id.cancel_order)
    Button cancelOrder;
    ServicePartWithoutViewHolder servicePartViewHolder = new ServicePartWithoutViewHolder();
    private OrderItem mOrderInfo;
    private AsyncHttpClient mClient = new AsyncHttpClient();

    public static void start(Activity context, OrderItem orderItem) {
        Intent intent = new Intent(context, CancelOrderActivity.class);
        intent.putExtra(ORDER_KEY, orderItem);
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    public static boolean isCanceled(int requestCode, int resultCode, Intent data) {
        return requestCode == REQUEST_CODE && resultCode == RESULT_OK;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mOrderInfo = (OrderItem) intent.getSerializableExtra(ORDER_KEY);
        if (mOrderInfo == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(R.string.ct_order_cancel);
        View view = LayoutInflater.from(this).inflate(R.layout.ct_order_cancel, null);
        setContentView(view);
        ButterKnife.inject(this);
        servicePartViewHolder.build(view);
        servicePartViewHolder.render(ServicePartRenderData.getInstance(mOrderInfo));
    }


    @OnClick({R.id.ct_finder_cancel_rb, R.id.ct_travel_change_rb})
    public void hidenCOntent() {
        content.setVisibility(View.GONE);
    }


    @OnClick(R.id.ct_other_rb)
    public void showContent() {
        content.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_order:
                String message = null;
                if (TextUtils.isEmpty(content.getText())) {
                    MessageUtils.showToast(R.string.ct_cancel_null_reason);
                    return;
                }
                message = content.getText().toString();

                final String msg = message;
                MessageUtils.dialogBuilder(CancelOrderActivity.this, true, null, "确定要取消订单吗", null, getString(R.string.ct_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(msg);
                    }
                });
                break;
        }
    }

    private void cancelOrder(String msg) {
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
        }, mOrderInfo.getOid(), msg);
    }

}
