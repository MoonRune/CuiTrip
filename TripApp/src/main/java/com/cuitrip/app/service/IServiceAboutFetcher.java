package com.cuitrip.app.service;

import com.cuitrip.app.base.CtFetchCallback;

/**
 * Created by baziii on 15/8/14.
 */
public interface IServiceAboutFetcher {
    void fetchServiceAbout(String serviceId,CtFetchCallback<ServiceAboutMode> callback);
}
