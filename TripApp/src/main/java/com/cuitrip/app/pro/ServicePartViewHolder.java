package com.cuitrip.app.pro;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuitrip.service.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/10.
 */
public class ServicePartViewHolder {
    public static final int RES = R.layout.ct_service_part;
    @InjectView(R.id.ct_service_name_tv)
    TextView mServiceNameTv;
    @InjectView(R.id.ct_order_status_tv)
    TextView mOrderStatusTv;
    @InjectView(R.id.ct_order_time_tv)
    TextView mOrderTimeTv;
    @InjectView(R.id.ct_person_size_tv)
    TextView mPersonSizeTv;
    @InjectView(R.id.ct_service_duration_tv)
    TextView mServiceDurationTv;
    @InjectView(R.id.ct_order_price_tv)
    TextView mOrderPriceTv;
    @InjectView(R.id.ct_service_duration_layout)
    LinearLayout mServiceDurationLayout;
    @InjectView(R.id.ct_ct_service_duration_divide)
    View mCtServiceDurationDivide;

    public ServicePartViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

    public void render(ServicePartRenderData data) {
        mServiceNameTv.setText(data.getServiceName());
        mOrderStatusTv.setText(data.getOrderStatus());
        if (!TextUtils.isEmpty(data.getServiceDuration())) {
            mServiceDurationTv.setText(data.getServiceDuration());
        }
        mOrderTimeTv.setText(data.getOrderDate());
        mOrderPriceTv.setText(data.getPriceWithCurrency());
    }
}
