package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cuitrip.business.OrderBusiness;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created on 7/21.
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private OrderItem mOrderInfo;
    private AsyncHttpClient mClient = new AsyncHttpClient();
    private TextView mContent;
    private RatingBar mRatingBar;


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
        showActionBar(R.string.ct_order_comment);
        setContentView(R.layout.ct_order_comment);
        setViewText(R.id.service_name, mOrderInfo.getServiceName());
        setViewText(R.id.cuthor_name, mOrderInfo.getInsiderName());
        setViewText(R.id.service_address, mOrderInfo.getServiceAddress());
        ImageHelper.displayPersonImage(mOrderInfo.getHeadPic(), (ImageView) findViewById(R.id.author_img), null);
        findViewById(R.id.commit_comment).setOnClickListener(this);
        mContent = (TextView) findViewById(R.id.content);
        mRatingBar = (RatingBar) findViewById(R.id.comment_score);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit_comment:
                commitComment();
                break;
        }
    }

    private void commitComment() {
        showNoCancelDialog();
        OrderBusiness.submitReview(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideNoCancelDialog();
                MessageUtils.showToast(R.string.ct_order_comment_suc);
                Intent intent = new Intent(CommentActivity.this, CommentSuccessActivity.class)
                        .putExtra(OrderDetailActivity.ORDER_INFO, mOrderInfo);
                if(mRatingBar.getRating() > 3){
                    intent.putExtra(CommentSuccessActivity.ENABLE_SHARE, true);
                }
                LocalBroadcastManager.getInstance(CommentActivity.this).sendBroadcast(
                        new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
                startActivity(intent);
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
        }, mOrderInfo.getOid(), String.valueOf(mRatingBar.getRating()), mContent.getText().toString());
    }
}
