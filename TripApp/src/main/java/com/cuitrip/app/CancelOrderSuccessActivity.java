package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;

/**
 * Created on 7/22.
 */
public class CancelOrderSuccessActivity extends BaseActivity implements View.OnClickListener {
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
        showActionBar(R.string.ct_cancel_suc);
        setContentView(R.layout.ct_order_cancel_sux);
        setViewText(R.id.service_name, mOrderInfo.getServiceName());
        setViewText(R.id.cuthor_name, mOrderInfo.getInsiderName());
        setViewText(R.id.service_address, mOrderInfo.getServiceAddress());
        ImageHelper.displayPersonImage(mOrderInfo.getHeadPic(), (ImageView) findViewById(R.id.author_img), null);
        findViewById(R.id.go_order_list).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(CancelOrderSuccessActivity.this, IndexActivity.class)
                        .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.ORDER_TAB));
                finish();
            }
        });
        UserInfo userInfo = LoginInstance.getInstance(this).getUserInfo();
        if(userInfo.isTravel()){
            findViewById(R.id.go_recommend).setOnClickListener(this);
        }else{
            findViewById(R.id.go_recommend).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_recommend:
                startActivity(new Intent(this, IndexActivity.class)
                        .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.RECOMMEND_TAB));
                finish();
                break;
        }
    }
}
