package com.cuitrip.app.pro;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.cuitrip.app.base.PartViewHolder;
import com.cuitrip.service.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/10.
 */
public class ServicePartWithoutViewHolder implements PartViewHolder<ServicePartRenderData> {
    public static final int RES = R.layout.ct_service_part_little;
    @InjectView(R.id.ct_service_name_tv)
    TextView mServiceNameTv;
    @InjectView(R.id.ct_order_time_tv)
    TextView mOrderTimeTv;
    @InjectView(R.id.ct_person_size_tv)
    TextView mPersonSizeTv;
    @InjectView(R.id.ct_order_price_tv)
    TextView mOrderPriceTv;


    public void build(View view) {
        ButterKnife.inject(this, view);
    }
    public void build(Activity activity) {
        ButterKnife.inject(this, activity);
    }

    public void render(ServicePartRenderData data) {
        mServiceNameTv.setText(data.getServiceName());
        mOrderTimeTv.setText(data.getOrderDate());
        mOrderPriceTv.setText(data.getPriceWithCurrency());
        mPersonSizeTv.setText(data.getOrderPeopleSize());
    }
}
