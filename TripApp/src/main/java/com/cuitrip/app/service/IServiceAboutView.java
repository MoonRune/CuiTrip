package com.cuitrip.app.service;

import com.cuitrip.app.base.IRefreshView;

/**
 * Created by baziii on 15/8/14.
 */
public interface IServiceAboutView extends IRefreshView<ServiceAboutMode> {
    void jumpService(String  serviceId);
    void jumpFinderSetDate(String  serviceId);
}
