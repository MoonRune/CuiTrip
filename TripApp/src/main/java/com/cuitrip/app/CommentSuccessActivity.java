package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.MessageUtils;
import com.lab.utils.share.ShareUtil;

import java.util.Locale;

/**
 * Created on 7/26.
 */
public class CommentSuccessActivity extends BaseActivity implements View.OnClickListener {
    public static final String ORDER_INFO = "CommentSuccessActivity.ORDER_INFO";
    public static final String ENABLE_SHARE = "enable_share";

    private OrderItem mOrderInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mOrderInfo = (OrderItem) intent.getSerializableExtra(CommentSuccessActivity.ORDER_INFO);
        if (mOrderInfo == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(R.string.ct_order_comment_suc);
        setContentView(R.layout.ct_order_comment_suc);
        setViewText(R.id.service_name, mOrderInfo.getServiceName());
        setViewText(R.id.service_address, mOrderInfo.getServiceAddress());
        findViewById(R.id.go_share).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_share:

                startActivity(new Intent(this, IndexActivity.class)
                        .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.ORDER_TAB));
                break;
        }
    }

    private void share() {
        ShareUtil.share(this, findViewById(R.id.service_name),
                mOrderInfo.getServiceName(), String.format(Locale.ENGLISH,
                        getString(R.string.ct_trip_share), mOrderInfo.getServiceName()),
                "http://www.cuitrip.com/mobile/serviceDetail.html?sid=" + mOrderInfo.getSid(), ""
        );
    }
}
