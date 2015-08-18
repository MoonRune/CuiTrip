package com.cuitrip.app.conversation.rong;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.model.OrderItem;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by baziii on 15/8/14.
 */
public class OrderNameFetcher {
    SyncHttpClient mClient = new SyncHttpClient();

    public void fetcherOrderName(final String id, final CtFetchCallback<OrderItem> fetchCallback) {
        OrderBusiness.getOrderInfo(MainApplication.getInstance(), mClient, new LabAsyncHttpResponseHandler(com.cuitrip.model.OrderItem.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null && data instanceof com.cuitrip.model.UserInfo) {
                    com.cuitrip.model.OrderItem temp = ((com.cuitrip.model.OrderItem) data);
                    fetchCallback.onSuc(temp);
                }else {
                    fetchCallback.onFailed(new CtException());
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                fetchCallback.onFailed(new CtException());
            }
        }, id);
    }
}
