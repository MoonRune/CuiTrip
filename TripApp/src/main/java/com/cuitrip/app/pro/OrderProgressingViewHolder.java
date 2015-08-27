package com.cuitrip.app.pro;

import android.view.View;
import android.widget.TextView;

import com.cuitrip.app.base.PartViewHolder;
import com.cuitrip.service.R;
import com.lab.utils.ImageHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by baziii on 15/8/10.
 */
public class OrderProgressingViewHolder implements PartViewHolder<OrderProgressingRenderData> {
    public static final int RES = R.layout.ct_order_progressing;
    @InjectView(R.id.ct_travel_ava_im)
    CircleImageView ctTravelAvaIm;
    @InjectView(R.id.ct_finder_ava_im)
    CircleImageView ctFinderAvaIm;
    @InjectView(R.id.ct_service_name_tv)
    TextView ctServiceNameTv;
    @InjectView(R.id.ct_service_address_tv)
    TextView ctServiceAddressTv;
    @InjectView(R.id.ct_travel_name_tv)
    TextView ctTravelNameTv;
    @InjectView(R.id.ct_finder_name_tv)
    TextView ctFinderNameTv;

    public void build(View view) {
        ButterKnife.inject(this, view);
    }

    public void render(OrderProgressingRenderData data) {
        ImageHelper.displayPersonImage(data.getTravelAva(), ctTravelAvaIm, null);
        ctTravelNameTv.setText(data.getTravelName());

        ImageHelper.displayPersonImage(data.getFinderAva(), ctFinderAvaIm, null);
        ctFinderNameTv.setText(data.getFinderName());
        ctServiceNameTv.setText(data.getServiceName());
        ctServiceAddressTv.setText(data.getServiceAddress());
    }


}
