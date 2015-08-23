package com.cuitrip.app.message;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.ListFetchCallback;
import com.cuitrip.business.MessageBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.MessageServerItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class MessagePresent {
    public static final String TAG = "MessagePresent";


    IMessageListView mMessageView;
    IMessageFetcher mMessageFetcher = new MessageFetcher();
    int userType;

    public MessagePresent(IMessageListView mMessageView) {
        this.mMessageView = mMessageView;
        userType = LoginInstance.isLogin(MainApplication.getInstance()) ?
                0 : (LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo().isTravel() ? UserInfo.USER_TRAVEL :
                UserInfo.USER_FINDER);
    }

    public class MessageFetcher implements IMessageFetcher {

        AsyncHttpClient mClient = new AsyncHttpClient();
        int defaultsize = 10;

        @Override
        public void getMessageList(final ListFetchCallback<MessageMode> itemListFetchCallback) {
            MessageBusiness.getMessageList(((MessageListActivity) mMessageView), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {

                    LogHelper.e("omg","res "+response.result);
                    List<MessageMode> result = new ArrayList<>();
                    try {
                        List<MessageServerItem> datas = JSON.parseArray(data.toString(), MessageServerItem.class);
                        LogHelper.e("omg","datas  "+datas.size());
                        for (MessageServerItem item : datas) {
                            result.add(MessageMode.getInstance(item));
                        }
                    } catch (Exception e) {
                        LogHelper.e("omg","eerror "+e.getMessage());
                    }
                    itemListFetchCallback.onSuc(result);
                }

                @Override
                public void onFailure(LabResponse response, Object data) {

                    LogHelper.e("omg","onFailure ");
                    String msg ;
                    if (response !=null &&!TextUtils.isEmpty(response.msg)){
                        msg = response.msg;
                    }else {
                        msg= PlatformUtil.getInstance().getString(R.string.data_error);
                    }
                    itemListFetchCallback.onFailed(new CtException(msg));
                }
            }, userType, 0, defaultsize);

        }

        @Override
        public void getMessageListWithMore(int pattern, final ListFetchCallback<MessageMode> itemListFetchCallback) {
            MessageBusiness.getMessageList(((MessageListActivity) mMessageView), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {

                    List<MessageMode> result = new ArrayList<>();
                    try {
                        List<MessageServerItem> datas = JSON.parseArray(data.toString(), MessageServerItem.class);
                        for (MessageServerItem item : datas) {
                            result.add(MessageMode.getInstance(item));
                        }
                    } catch (Exception e) {
                    }
                    itemListFetchCallback.onSuc(result);
                }

                @Override
                public void onFailure(LabResponse response, Object data) {

                    String msg;
                    if (response != null && !TextUtils.isEmpty(response.msg)) {
                        msg = response.msg;
                    } else {
                        msg = PlatformUtil.getInstance().getString(R.string.data_error);
                    }
                    itemListFetchCallback.onFailed(new CtException(msg));
                }
            }, userType, pattern, pattern+defaultsize);
        }

        @Override
        public void deleteMessage(String id, final CtApiCallback callback) {
            MessageBusiness.deleteMessage(((MessageListActivity) mMessageView), mClient, new LabAsyncHttpResponseHandler() {

                @Override
                public void onSuccess(LabResponse response, Object data) {
                    callback.onSuc();
                }

                @Override
                public void onFailure(LabResponse response, Object data) {

                    String msg;
                    if (response != null && !TextUtils.isEmpty(response.msg)) {
                        msg = response.msg;
                    } else {
                        msg = PlatformUtil.getInstance().getString(R.string.data_error);
                    }
                    callback.onFailed(new CtException(msg));
                }
            }, id);
        }
    }

    public void requestLoadMore() {
        LogHelper.e(TAG, "requestLoadMore");
        mMessageView.uiShowLoadMore();
        mMessageFetcher.getMessageListWithMore(mMessageView.getSize(), new ListFetchCallback<MessageMode>() {
            @Override
            public void onSuc(List<MessageMode> t) {
                LogHelper.e(TAG, "requestLoadMore onscu" + t.size());
                mMessageView.renderUIWithAppendData(t);
                mMessageView.uiHideLoadMore();
            }

            @Override
            public void onFailed(Throwable throwable) {

                LogHelper.e(TAG, "requestLoadMore onfailed");
                MessageUtils.showToast(throwable.getMessage());
                mMessageView.uiHideLoadMore();
            }
        });
    }

    public void onClickMessage(final MessageMode messageMode) {
        mMessageView.jumpMessage(messageMode);
    }

    public void onMove(final MessageMode messageMode) {
        mMessageFetcher.deleteMessage(messageMode.getId(), new CtApiCallback() {
            @Override
            public void onSuc() {
                mMessageView.deleteMessage(messageMode);
            }

            @Override
            public void onFailed(CtException throwable) {
                MessageUtils.showToast(throwable.getMessage());
            }
        });
    }

    public void requestRefresh() {
        mMessageView.uiShowRefreshLoading();
        LogHelper.e(TAG, "requestRefresh");
        mMessageFetcher.getMessageList(new ListFetchCallback<MessageMode>() {
            @Override
            public void onSuc(List<MessageMode> t) {
                LogHelper.e(TAG, "requestRefresh onsuc" + t.size());
                mMessageView.renderUIWithData(t);
                mMessageView.uiHideRefreshLoading();
            }

            @Override
            public void onFailed(Throwable throwable) {
                LogHelper.e(TAG, "requestRefresh onfailed");
                MessageUtils.showToast(throwable.getMessage());
                mMessageView.uiHideRefreshLoading();
            }
        });
    }
}
