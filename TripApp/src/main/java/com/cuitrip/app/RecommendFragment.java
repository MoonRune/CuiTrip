package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cuitrip.adapter.RecommendAdapter;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.model.RecommendItem;
import com.cuitrip.model.RecommendOutData;
import com.lab.app.BaseFragment;
import com.cuitrip.service.R;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;


import java.util.List;

public class RecommendFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "RecommendFragment";
    private SwipeRefreshLayout refresh;
    private View mContentView;
    private ListView mListView;
    private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    private RecommendOutData mRecommendOutData;
    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public void onResume(){
        super.onResume();
        showActionBar(getString(R.string.ct_hot_recommend));
        if(mRecommendOutData == null || mRecommendOutData.getLists() == null
                || mRecommendOutData.getLists().isEmpty()){
            mContentView.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                    //TODO TW
                    ServiceBusiness.getRecommendList(getActivity(), mAsyncHttpClient, responseHandler,
                            "TW", null, 0, RecommendAdapter.PAGE_SIZE);
                }
            });
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if(refresh != null){
            refresh.setRefreshing(false);
        }
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.ct_recommend_list_view, container, false);
            mListView = (ListView) mContentView.findViewById(R.id.ct_list);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (getActivity() != null && mRecommendOutData != null
                            && mRecommendOutData.getLists() != null) {
                        if (position < mRecommendOutData.getLists().size() && position > -1) {
                            RecommendItem item = mRecommendOutData.getLists().get(position);
                            startActivity(new Intent(getActivity(), ServiceDetailActivity.class)
                                    .putExtra(ServiceDetailActivity.SERVICE_ID, item.getSid()));
                        }
                    }
                }
            });
            refresh = (SwipeRefreshLayout) mContentView.findViewById(R.id.refresh_layout);
            refresh.setOnRefreshListener(RecommendFragment.this);
        } else {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
        }
        return mContentView;
    }

    LabAsyncHttpResponseHandler responseHandler = new LabAsyncHttpResponseHandler(RecommendOutData.class) {
        @Override
        public void onFailure(LabResponse response, Object data) {

                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                }
                refresh.setRefreshing(false);
                onNetwokError(0, 0, 0);
        }

        @Override
        public void onSuccess(LabResponse response, Object data) {
            LogHelper.w(TAG, response.toString());
            refresh.setRefreshing(false);
            if (data != null) {
                mRecommendOutData = (RecommendOutData) data;
                List<RecommendItem> list = mRecommendOutData.getLists();
                if (list != null && !list.isEmpty()) {
                    RecommendAdapter adapter = new RecommendAdapter(getActivity(),
                            list, R.layout.ct_list_pending, list.size() <= RecommendAdapter.PAGE_SIZE);
                    mListView.setAdapter(adapter);
                }
            }
        }
    };

    @Override
    public void onRefresh() {
        refresh.setRefreshing(true);
        ServiceBusiness.getRecommendList(getActivity(), mAsyncHttpClient, responseHandler,
                "TW", null, 0, RecommendAdapter.PAGE_SIZE);
    }
}
