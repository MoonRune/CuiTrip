package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.share.ShareUtil;

import java.util.Locale;

/**
 * Created on 7/26.
 */
public class CommentSuccessActivity extends BaseActivity implements View.OnClickListener {
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
        mOrderInfo = (OrderItem) intent.getSerializableExtra(OrderDetailActivity.ORDER_INFO);
        if (mOrderInfo == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(R.string.ct_order_comment_suc);
        setContentView(R.layout.ct_order_comment_suc);
        setViewText(R.id.service_name, mOrderInfo.getServiceName());
        setViewText(R.id.cuthor_name, mOrderInfo.getInsiderName());
        setViewText(R.id.service_address, mOrderInfo.getServiceAddress());
        ImageHelper.displayPersonImage(mOrderInfo.getHeadPic(), (ImageView) findViewById(R.id.author_img), null);
        if (intent.getBooleanExtra(ENABLE_SHARE, false)) {
            findViewById(R.id.go_share).setOnClickListener(this);
        } else {
            findViewById(R.id.go_share).setVisibility(View.GONE);
        }
        findViewById(R.id.go_recommend).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_recommend:
                startActivity(new Intent(this, IndexActivity.class)
                        .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.RECOMMEND_TAB));
                finish();
                break;
            case R.id.go_share:
                share();
                break;
        }
    }

    private void share() {

        ShareUtil.share(this, findViewById(R.id.service_pic),
                mOrderInfo.getServiceName(), String.format(Locale.ENGLISH,
                        getString(R.string.ct_trip_share), mOrderInfo.getServiceName()),
                "http://www.cuitrip.com/mobile/serviceDetail.html?sid=" + mOrderInfo.getSid(), ""
        );
    }
}
