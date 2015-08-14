package com.cuitrip.app.service;

import android.os.Bundle;

import com.cuitrip.app.ServiceDetailActivity;
import com.lab.app.BaseActivity;
import com.lab.app.DateActivity;

/**
 * Created by baziii on 15/8/14.
 */
public class ServiceAboutActivity extends BaseActivity implements IServiceAboutView {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void jumpService(String serviceId) {
        ServiceDetailActivity.start(this,serviceId);
    }

    @Override
    public void jumpFinderSetDate(String serviceId) {
        DateActivity.startFinder(this,serviceId);
    }

    @Override
    public void uiShowRefreshLoading() {
        showLoading();
    }

    @Override
    public void uiHideRefreshLoading() {
        hideLoading();
    }

    @Override
    public void renderUIWithData(ServiceAboutMode item) {

    }

    @Override
    public void disableEvent() {

    }

    @Override
    public void enableEvent() {

    }
}
