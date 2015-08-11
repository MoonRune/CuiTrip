package com.cuitrip.app.pro;

import android.view.View;

/**
 * Created by baziii on 15/8/10.
 */
public class ServicePartWithoutDurationViewHolder extends ServicePartViewHolder {

    public ServicePartWithoutDurationViewHolder(View view) {
        super(view);
        mServiceDurationLayout.setVisibility(View.GONE);
        mCtServiceDurationDivide.setVisibility(View.GONE);
    }

    public void render(ServicePartRenderData data) {
        super.render(data);
    }
}
