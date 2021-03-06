package com.cuitrip.app.pro;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuitrip.app.base.PartViewHolder;
import com.cuitrip.app.map.GaoDeMapActivity;
import com.cuitrip.service.R;
import com.lab.utils.LogHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/10.
 */
public class ServicePartViewHolder implements PartViewHolder<ServicePartRenderData> {
    public static final int RES = R.layout.ct_service_part;
    @InjectView(R.id.ct_service_name_tv)
    TextView mServiceNameTv;
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
    @InjectView(R.id.ct_service_meet_location_tv)
    TextView ctServiceMeetLocationTv;
    @InjectView(R.id.ct_service_meet_location_layout)
    LinearLayout ctServiceMeetLocationLayout;
    @InjectView(R.id.ct_order_price_include_tv)
    TextView ctOrderPriceIncludeTv;
    @InjectView(R.id.ct_order_price_uninclude_tv)
    TextView ctOrderPriceUnincludeTv;
    @InjectView(R.id.ct_order_price_include_ll)
    LinearLayout ctOrderPriceIncludeLl;
    @InjectView(R.id.ct_order_price_uninclude_ll)
    LinearLayout ctOrderPriceUnincludeLl;
    @InjectView(R.id.ct_order_unvaliable_tv)
    TextView ctOrderUnvaliableTv;
    @InjectView(R.id.ct_order_unvaliable_ll)
    LinearLayout ctOrderUnvaliableLl;

    public void build(View view) {
        ButterKnife.inject(this, view);
    }

    public void build(Activity activity) {
        ButterKnife.inject(this, activity);
    }

    public void render(final ServicePartRenderData data) {
        LogHelper.e("omg", data.toString());
        mServiceNameTv.setText(data.getServiceName());
        if (!TextUtils.isEmpty(data.getServiceDuration())) {
            mServiceDurationTv.setText(data.getServiceDuration());
        }
        mOrderTimeTv.setText(data.getOrderDate());
        mOrderPriceTv.setText(data.getPriceWithCurrency());
        mPersonSizeTv.setText(data.getOrderPeopleSize());
        ctServiceMeetLocationTv.setText(data.getMeetLocation());
        ctOrderPriceIncludeTv.setText(data.getPriceInclude());
        ctOrderPriceUnincludeTv.setText(data.getPriceUninclude());
        if (!TextUtils.isEmpty(data.getUnvaliableReason())) {
            ctOrderUnvaliableLl.setVisibility(View.VISIBLE);
            ctOrderUnvaliableTv.setText(data.getUnvaliableReason());
        } else {
            ctOrderUnvaliableLl.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(data.getMeetLocation())) {
            ctServiceMeetLocationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else {
            ctServiceMeetLocationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GaoDeMapActivity.startShow(v.getContext(), data.getLat(), data.getLng(), data.getMeetLocation());
                }
            });
        }
    }

}
