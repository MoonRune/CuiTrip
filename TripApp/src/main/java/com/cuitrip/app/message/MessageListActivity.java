package com.cuitrip.app.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cuitrip.app.base.BaseVerticalListActivity;
import com.lab.utils.LogHelper;

import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class MessageListActivity extends BaseVerticalListActivity implements IMessageView {
    public static final String TAG = "MessageListActivity";
    MessagePresent mMessagePresent = new MessagePresent(this);
    MessageAdapter mAdapter = new MessageAdapter();

    public static void start(Context context) {
        if (context != null) {
            context.startActivity(new Intent(context, MessageListActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);
        LogHelper.e(TAG,"oncreate callrefresh");
        requestPresentRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogHelper.e(TAG," is ui show refresh:"+mSwipeRefreshLayout.isRefreshing());
        mSwipeRefreshLayout.refreshDrawableState();
    }

    @Override
    public void jumpMessage(MessageMode messageMode) {

    }

    @Override
    public void moveLeftMessage(MessageMode messageMode) {

    }

    @Override
    public void uiShowLoadMore() {
        //foot view show
    }

    @Override
    public void uiHideLoadMore() {
        //foot view hiden

    }

    @Override
    public void renderUIWithData(List<MessageMode> items) {
        mAdapter.setModeList(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void disableEvent() {

    }

    @Override
    public void enableEvent() {

    }

    @Override
    public void requestPresentLoadMore() {
//        mMessagePresent.requestLoadMore();
    }

    @Override
    public void requestPresentRefresh() {
        mMessagePresent.requestRefresh();

    }
}
