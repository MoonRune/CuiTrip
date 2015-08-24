package com.cuitrip.app.service;

import android.text.TextUtils;

import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.model.ServiceDetail;
import com.cuitrip.model.ServiceStatistic;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.loopj.android.http.AsyncHttpClient;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by baziii on 15/8/14.
 */
public class ServiceAboutPresent {
    IServiceAboutView iServiceAboutView;
    String serviceId;

    public ServiceAboutPresent(IServiceAboutView iServiceAboutView, String serviceId) {
        this.iServiceAboutView = iServiceAboutView;
        this.serviceId = serviceId;
    }

    public ServiceDetail detail;
    public ServiceStatistic statistic;
    IServiceAboutFetcher serviceAboutFetcher = new IServiceAboutFetcher() {
        AsyncHttpClient mClinet = new AsyncHttpClient();

        @Override
        public void fetchServiceAbout(String serviceId, final CtFetchCallback<ServiceAboutMode> callback) {
            final AtomicInteger atomicInteger = new AtomicInteger(2);
            ServiceBusiness.getServiceDetail(((ServiceAboutActivity) iServiceAboutView),
                    mClinet, new LabAsyncHttpResponseHandler(ServiceDetail.class) {
                        @Override
                        public void onSuccess(LabResponse response, Object data) {
                            detail = ((ServiceDetail) data);
                            atomicInteger.decrementAndGet();
                            notifySuc(atomicInteger,callback);
                        }

                        @Override
                        public void onFailure(LabResponse response, Object data) {
                            String msg;
                            atomicInteger.decrementAndGet();
                            if (response != null && !TextUtils.isEmpty(response.msg)) {
                                msg = response.msg;
                            } else {
                                msg = PlatformUtil.getInstance().getString(R.string.ct_fetch_service_detail_failed_error);
                            }
                            notifyOnfailed(msg, atomicInteger, callback);
                        }
                    }, serviceId, UnitUtils.getSettingMoneyType());
            ServiceBusiness.getStatistic(((ServiceAboutActivity) iServiceAboutView),
                    mClinet, new LabAsyncHttpResponseHandler(ServiceStatistic.class) {
                        @Override
                        public void onSuccess(LabResponse response, Object data) {
                            statistic = ((ServiceStatistic) data);
                            atomicInteger.decrementAndGet();
                            notifySuc(atomicInteger,callback);
                        }

                        @Override
                        public void onFailure(LabResponse response, Object data) {
                            String msg;
                            if (response != null && !TextUtils.isEmpty(response.msg)) {
                                msg = response.msg;
                            } else {
                                msg = PlatformUtil.getInstance().getString(R.string.ct_fetch_service_detail_failed_error);
                            }
                            atomicInteger.decrementAndGet();
                            notifyOnfailed(msg,atomicInteger,callback);
                        }
                    }, serviceId);
        }

        public void notifySuc(AtomicInteger atomicInteger, final CtFetchCallback<ServiceAboutMode> callback) {
            if (atomicInteger.get() == 0) {
                if (detail != null && statistic != null) {
                    callback.onSuc(ServiceAboutMode.getInstance(detail, statistic));
                } else {
                    callback.onSuc(ServiceAboutMode.getInstance(detail, statistic));
                    callback.onFailed(new CtException(PlatformUtil.getInstance().getString(R.string.ct_fetch_service_detail_failed_error)));
                }
            }
        }

        public void notifyOnfailed(String msg, AtomicInteger atomicInteger, final CtFetchCallback<ServiceAboutMode> callback) {
            if (atomicInteger.get() == 0) {
                callback.onFailed(new CtException(msg));
            }
        }
    };

    public void requestServiceAboutData() {
        iServiceAboutView.uiShowRefreshLoading();
        serviceAboutFetcher.fetchServiceAbout(serviceId, new CtFetchCallback<ServiceAboutMode>() {
            @Override
            public void onSuc(ServiceAboutMode serviceAboutMode) {
                iServiceAboutView.renderUIWithData(serviceAboutMode);
                iServiceAboutView.uiHideRefreshLoading();

            }

            @Override
            public void onFailed(CtException throwable) {
                iServiceAboutView.showError(throwable.getMessage());
                iServiceAboutView.uiHideRefreshLoading();
            }
        });
    }

    public void clickModify() {
        if (detail!=null) {
            iServiceAboutView.jumpModifyService(detail.getServiceInfo());
        }
    }

    public void clickService() {
        iServiceAboutView.jumpService(serviceId);
    }

    public void clickSetDate() {
        iServiceAboutView.jumpFinderSetDate(serviceId);
    }
}
