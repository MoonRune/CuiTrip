package com.cuitrip.app.pro;

import android.view.View;

/**
 * Created by baziii on 15/8/10.
 */
public class ServicePartWithoutViewHolder extends ServicePartViewHolder {

    public void build(View view) {
        super.build(view);
        mServiceDurationLayout.setVisibility(View.GONE);
        mCtServiceDurationDivide.setVisibility(View.GONE);
        ctServiceMeetLocationLayout.setVisibility(View.GONE);
        ctOrderPriceIncludeLl.setVisibility(View.GONE);
        ctOrderPriceUnincludeLl.setVisibility(View.GONE);
    }

    public void render(ServicePartRenderData data) {
        super.render(data);
    }
}
