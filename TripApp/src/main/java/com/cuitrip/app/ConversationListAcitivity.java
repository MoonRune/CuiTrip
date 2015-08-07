package com.cuitrip.app;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cuitrip.conversation.ConversationAdapter;
import com.cuitrip.conversation.ConversationItem;
import com.cuitrip.conversation.ConversationsPresent;
import com.cuitrip.conversation.IConversationsView;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/6.
 */
public class ConversationListAcitivity extends BaseActivity implements IConversationsView {
    @InjectView(R.id.ct_recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.ct_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager mLayoutManager;
    ConversationAdapter mAdapter;
    ConversationsPresent mPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationlist); /*加载您上面的 conversationlist */
        ButterKnife.inject(this);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPresent = new ConversationsPresent(this);
        mPresent.onCallRefresh();

    }

    @Override
    public void showRefreshLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshLoading() {
        mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void disableEvent() {

    }

    @Override
    public void enableEvent() {

    }

    @Override
    public void showLoadMore() {

    }

    @Override
    public void hideLoadMore() {

    }

    @Override
    public void jumpConversation(ConversationItem item) {
            ConversationAcitivity.startActivity(this,item.getId(),item.getName());
    }

    @Override
    public void removeItem(ConversationItem item) {

    }

    @Override
    public void refreshMessage(List<ConversationItem> items) {
        if (mAdapter == null) {
            mAdapter = new ConversationAdapter(mPresent);
            mAdapter.setDatas(items);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDatas(items);
            mAdapter.notifyDataSetChanged();
        }
    }
}
