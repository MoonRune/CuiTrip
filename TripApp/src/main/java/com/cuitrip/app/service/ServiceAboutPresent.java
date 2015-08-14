package com.cuitrip.app.service;

import android.os.AsyncTask;

import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.CtFetchCallback;

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

    IServiceAboutFetcher serviceAboutFetcher = new IServiceAboutFetcher() {
        @Override
        public void fetchServiceAbout(String serviceId, final CtFetchCallback<ServiceAboutMode> callback) {
            new AsyncTask() {
                ServiceAboutMode serviceMode;
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        serviceMode = new ServiceAboutMode("ava","name","create","todyvisit","wholevisit","todyvisitPeople",
                                "wholevisitp","likes","whoeorder","payorder","overorder","cancelorder");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    callback.onSuc(serviceMode);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    public void requestServiceAboutData(){
        iServiceAboutView.uiShowRefreshLoading();
        serviceAboutFetcher.fetchServiceAbout(serviceId, new CtFetchCallback<ServiceAboutMode>() {
            @Override
            public void onSuc(ServiceAboutMode serviceAboutMode) {
                iServiceAboutView.renderUIWithData(serviceAboutMode);
                iServiceAboutView.uiHideRefreshLoading();

            }

            @Override
            public void onFailed(CtException throwable) {
                iServiceAboutView.uiHideRefreshLoading();
            }
        });
    }

    public void clickService(){
        iServiceAboutView.jumpService(serviceId);
    }

    public void clickSetDate(){
        iServiceAboutView.jumpFinderSetDate(serviceId);
    }
}
