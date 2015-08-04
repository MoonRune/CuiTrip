package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cuitrip.adapter.ReviewAdapter;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.model.ReviewList;
import com.cuitrip.model.ReviewListItem;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/17.
 */
public class ViewReviewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ViewReviewsActivity";
    //    private SwipeRefreshLayout refresh;
    private ListView mListView;
    private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
    private String sid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null || TextUtils.isEmpty(intent.getStringExtra(ServiceDetailActivity.SERVICE_ID))) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        sid = intent.getStringExtra(ServiceDetailActivity.SERVICE_ID);
        showActionBar(R.string.ct_all_reviews);
        setContentView(R.layout.ct_review_list);
        mListView = (ListView) findViewById(R.id.ct_list);
        showLoading();
        OrderBusiness.getReviewList(this, mAsyncHttpClient, mHandler, sid);
    }

    protected void onDestroy(){
        super.onDestroy();
        mAsyncHttpClient.cancelAllRequests(true);
    }
    @Override
    public void onRefresh() {
//        refresh.setRefreshing(true);
//        OrderBusiness.getReviewList(this, mAsyncHttpClient, mHandler, sid);
//        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
    }

    private LabAsyncHttpResponseHandler mHandler = new LabAsyncHttpResponseHandler() {
        @Override
        public void onSuccess(LabResponse response, Object data) {
//            refresh.setRefreshing(false);
            hideLoading();
            List<ReviewListItem> list = null;
            if (data != null) {
                try{
                    if(data instanceof JSONArray){
                        list = JSONArray.parseArray(data.toString(), ReviewListItem.class);
                    }else{
                        ReviewList reviewList = JSON.parseObject(data.toString(), ReviewList.class);
                        list = reviewList.getList();
                    }
                }catch (Exception e){
                }
                if (list != null && !list.isEmpty()) {
                    ReviewAdapter adapter = new ReviewAdapter(ViewReviewsActivity.this, R.layout.ct_review_item);
                    adapter.setData(list);
                    mListView.setAdapter(adapter);
                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                }
            } else {
                findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFailure(LabResponse response, Object data) {
            hideLoading();
            if (response != null && !TextUtils.isEmpty(response.msg)) {
                MessageUtils.showToast(response.msg);
            }
            onNetwokError(0, 0, 0);
        }
    };
}
