package com.cuitrip.app.conversation.rong;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.model.UserInfo;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by baziii on 15/8/14.
 */
public class UserNameAvaFetcher {
    SyncHttpClient mClient = new SyncHttpClient();

    public void fetchUseNameAva(final String id, final CtFetchCallback<UserInfo> fetchCallback) {
        UserBusiness.getUserInfo(MainApplication.getInstance(), mClient, new LabAsyncHttpResponseHandler(com.cuitrip.model.UserInfo.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null && data instanceof com.cuitrip.model.UserInfo) {
                    com.cuitrip.model.UserInfo temp = ((com.cuitrip.model.UserInfo) data);
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
