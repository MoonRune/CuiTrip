package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cuitrip.model.OrderItem;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;

/**
 * Created on 7/18.
 */
public class CreateorderSuccessActivity extends BaseActivity{

    public static final String ORDER_INFO = "order_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_create_order_suc);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        OrderItem item = (OrderItem) intent.getSerializableExtra(ORDER_INFO);
        if (item == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        setViewText(R.id.service_name, item.getServiceName());
        ImageHelper.displayCtImage(item.getServicePIC(),
                (ImageView) findViewById(R.id.service_img), null);

        findViewById(R.id.go_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateorderSuccessActivity.this, IndexActivity.class)
                        .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.ORDER_TAB));
                finish();
            }
        });
    }
}
