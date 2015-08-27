package com.cuitrip.app.service;

import com.cuitrip.app.base.IRefreshView;
import com.cuitrip.model.ServiceInfo;

/**
 * Created by baziii on 15/8/14.
 */
public interface IServiceAboutView extends IRefreshView<ServiceAboutMode> {
    void jumpService(String  serviceId);
    void jumpFinderSetDate(String  serviceId);
    void showError(String error);
    void jumpModifyService(ServiceInfo info);

}
