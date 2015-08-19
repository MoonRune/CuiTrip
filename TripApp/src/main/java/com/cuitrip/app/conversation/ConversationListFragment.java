package com.cuitrip.app.conversation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    LinearLayoutManager mLayoutManager;
    ConversationAdapter mAdapter;
    ConversationsPresent mPresent;


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
        if (LoginInstance.isLogin(MainApplication.getInstance())) {
            mPresent.onCallRefresh();
        }
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
    public void uiShowNoLogin() {
        ctLogin.setVisibility(View.VISIBLE);
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
    public void clickLogin(){
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
            ctLogin.setVisibility(View.GONE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
