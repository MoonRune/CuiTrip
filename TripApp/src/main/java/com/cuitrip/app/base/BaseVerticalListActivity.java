package com.cuitrip.app.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.LogHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/10.
 */
public abstract class BaseVerticalListActivity<T> extends BaseActivity implements ILoadMoreView<T> {
    public static final String TAG = "BaseVerticalListActivity";
    @InjectView(R.id.ct_recycler_view)
    protected RecyclerView mRecyclerView;
    @InjectView(R.id.ct_swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager mLayoutManager;

    //包括刷新和loadMore
    boolean loading = false;
    boolean hasMore = true;
    boolean isResumeRefresh = false;

    protected RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int pastVisiblesItems, visibleItemCount, totalItemCount;

            visibleItemCount = mLayoutManager.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
            if (hasMore && !loading) {
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    requestPresentLoadMore();
                }
            }
        }
    };
    protected SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            LogHelper.e(TAG, "ui onrefresh call");
            requestPresentRefresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_refresh_vertical);
        ButterKnife.inject(this);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(onScrollListener);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogHelper.e(TAG, " is ui show refresh:" + mSwipeRefreshLayout.isRefreshing());
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
            isResumeRefresh = true;
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isResumeRefresh) {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                }
            }, 300);
        }
    }

    public void onLoadStarted(){
        loading = true;
    }

    public void onLoadFinished(){
        loading = false;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public abstract void requestPresentLoadMore();

    public abstract void requestPresentRefresh();

    public void uiShowRefreshLoading() {
        LogHelper.e(TAG, "uiShowRefreshLoading");
        mSwipeRefreshLayout.setRefreshing(true);
        onLoadStarted();
    }

    public void uiHideRefreshLoading() {
        LogHelper.e(TAG, "uiHideRefreshLoading");
        mSwipeRefreshLayout.setRefreshing(false);
        isResumeRefresh = false;
        onLoadFinished();
    }

    @Override
    public void uiShowLoadMore() {
        onLoadStarted();
        //foot view show
    }

    @Override
    public void uiHideLoadMore() {
        onLoadFinished();
        //foot view hiden
    }

    @Override
    public void disableEvent() {

    }

    @Override
    public void enableEvent() {

    }

}
