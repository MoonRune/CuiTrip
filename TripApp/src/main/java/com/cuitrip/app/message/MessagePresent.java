package com.cuitrip.app.message;

import android.os.AsyncTask;

import com.cuitrip.base.ListFetchCallback;
import com.lab.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class MessagePresent {
    public static final String TAG = "MessagePresent";


    IMessageView mMessageView;
    IMessageFetcher mMessageFetcher = new TestMessageFetcher();
    String loadMorePattern;

    public MessagePresent(IMessageView mMessageView) {
        this.mMessageView = mMessageView;
    }

    public class TestMessageFetcher implements IMessageFetcher {
        @Override
        public void getMessageList(final ListFetchCallback<MessageMode> itemListFetchCallback) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    List<MessageMode> result = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        result.add(new MessageMode("name" + i, String.valueOf(i), "1991-3-" + i, "data " + i));
                    }
                    itemListFetchCallback.onSuc(result);
                    super.onPostExecute(o);
                }
            }.execute();
        }

        @Override
        public void getMessageListWithMore(String pattern, ListFetchCallback<MessageMode> itemListFetchCallback) {

        }
    }

    public void requestLoadMore() {
        LogHelper.e(TAG, "requestLoadMore");
        mMessageView.uiShowRefreshLoading();
        mMessageFetcher.getMessageListWithMore(loadMorePattern, new ListFetchCallback<MessageMode>() {
            @Override
            public void onSuc(List<MessageMode> t) {
                LogHelper.e(TAG, "requestLoadMore onscu" + t.size());
                mMessageView.renderUIWithData(t);
                mMessageView.uiHideRefreshLoading();
            }

            @Override
            public void onFailed(Throwable throwable) {

                LogHelper.e(TAG, "requestLoadMore onfailed");
                mMessageView.uiHideRefreshLoading();
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
                mMessageView.uiHideRefreshLoading();
            }
        });
    }
}
