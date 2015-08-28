package com.cuitrip.app.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuitrip.app.IndexActivity;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.orderdetail.OrderFormActivity;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.service.R;
import com.lab.app.BaseFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/6.
 */
public class ConversationListFragment extends BaseFragment implements IConversationsView {
    @InjectView(R.id.ct_recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.ct_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.ct_login)
    TextView ctLoginTv;
    @InjectView(R.id.no_login)
    View ctLogin;
    @InjectView(R.id.ct_empty)
    LinearLayout ctEmpty;


    LinearLayoutManager mLayoutManager;
    ConversationAdapter mAdapter;
    ConversationsPresent mPresent;


    boolean isResumeRefresh = false;

    @OnClick(R.id.ct_go_recommend)
    public void gotoTab(){
        startActivity(new Intent(getActivity(), IndexActivity.class)
                .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.RECOMMEND_TAB));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversationlist, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mSwipeRefreshLayout.setEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPresent = new ConversationsPresent(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        showActionBar(getString(R.string.conversation_list_title));
        if (LoginInstance.isLogin(MainApplication.getInstance())) {
            mPresent.onCallRefresh();
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
            isResumeRefresh = true;
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefreshLayout != null && isResumeRefresh) {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                }
            }, 300);
        }
    }

    @Override
    public void showRefreshLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshLoading() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            isResumeRefresh = false;
        }

    }

    @Override
    public void uiShowNoLogin() {
        ctLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void uiHidenNoLogin() {
        ctLogin.setVisibility(View.GONE);
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

    @OnClick(R.id.ct_login)
    public void clickLogin() {
        reLogin();
    }

    @Override
    public void jumpConversation(ConversationItem item) {
//        LogHelper.e("jump", "" + item.getId() + "|" + "" + item.getName() + "|" + "" + item.getLastWords());
//        ConversationAcitivity.startActivity(getActivity(), item.getId(), item.getName());
        OrderFormActivity.start(getActivity(), item.getOrderId(), item.getId());
    }

    @Override
    public void removeItem(ConversationItem item) {

    }

    @Override
    public void refreshMessage(List<ConversationItem> items) {
        if (!isDetached()) {
            if (mRecyclerView != null) {
                if (items == null && items.isEmpty()) {
                    ctEmpty.setVisibility(View.VISIBLE);
                } else {
                    ctEmpty.setVisibility(View.GONE);
                }
                if (mAdapter == null || mRecyclerView.getAdapter() == null) {
                    mAdapter = new ConversationAdapter(mPresent);
                    mAdapter.setDatas(items);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setDatas(items);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
