package com.cuitrip.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cuitrip.app.orderdetail.OrderFormActivity;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.adapter.BaseListAdapter;
import com.lab.adapter.ViewHolder;
import com.lab.app.BaseFragment;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

/**
 * Created on 7/18.
 */
public class OrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "OrderFragment";

    public static final int DEFUALT_SIZE = 10;
    public static final String ORDER_STATUS_CHANGED_ACTION = "ct_order_status_changed";

    public static final int TYPE_TRAVEL = 1;
    public static final int TYPE_FINDER = 2;
    public static final String TYPE = "order_type";

    private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    private SwipeRefreshLayout mRefresh;
    private View mContentView;
    private ListView mOrderList;
    private View mNoLogin;
    private View mEmpty;
    private OrderAdapter mAdapter;
    private List<OrderItem> mOrderDatas;

    protected int mType = TYPE_TRAVEL;

    public static OrderFragment newInstance(int type) {
        OrderFragment fragment = new OrderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(ORDER_STATUS_CHANGED_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOrderStatusChangedReceiver, filter);
    }

    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mOrderStatusChangedReceiver);
    }

    public void onResume() {
        super.onResume();
        showActionBar(getString(R.string.ct_order));
        if (LoginInstance.getInstance(getActivity()).isLogin(getActivity())) {
            if (LoginInstance.getInstance(getActivity()).getUserInfo().isTravel()) {
                mType = TYPE_TRAVEL;
            } else {
                mType = TYPE_FINDER;
                mEmpty.findViewById(R.id.ct_go_recommend).setVisibility(View.GONE);
            }
            if (mOrderDatas == null || mOrderDatas.isEmpty()) {
                mContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        getOrderList();
                    }
                });
            } else {
                //不同用户登录刷新页面
                String orderUser = mType == TYPE_TRAVEL ? mOrderDatas.get(0).getTravellerId()
                        : mOrderDatas.get(0).getInsiderId();
                String currentUser = LoginInstance.getInstance(getActivity()).getUserInfo().getUid();
                if (!currentUser.equals(orderUser)) {
                    mOrderDatas.clear();
                    mAdapter.notifyDataSetChanged();
                    mContentView.post(new Runnable() {
                        @Override
                        public void run() {
                            getOrderList();
                        }
                    });
                }
            }
            mNoLogin.setVisibility(View.GONE);
        } else {
            mNoLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.ct_order_ist, null);
            mOrderList = (ListView) mContentView.findViewById(R.id.ct_list);
            mRefresh = (SwipeRefreshLayout) mContentView.findViewById(R.id.refresh_layout);
            mRefresh.setOnRefreshListener(this);
            mOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    OrderFormActivity.start(getActivity(), mAdapter.getItem(i).getOid());
                }
            });
            mOrderList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (mOrderList != null && mAdapter != null && mAdapter.getCount() > 0) {
                        if (firstVisibleItem + visibleItemCount > totalItemCount - 2) {
                            loadMore();
                        }
                    }
                }
            });
            mNoLogin = mContentView.findViewById(R.id.ct_no_login);
            mEmpty = mContentView.findViewById(R.id.ct_empty);
            if (mType == TYPE_TRAVEL) {
                mEmpty.findViewById(R.id.ct_go_recommend).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), IndexActivity.class)
                                .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.RECOMMEND_TAB));
                    }
                });
            } else {
                mEmpty.findViewById(R.id.ct_go_recommend).setVisibility(View.GONE);
            }

            mAdapter = new OrderAdapter(getActivity(), R.layout.ct_order_list_item);
        }
        mContentView.findViewById(R.id.ct_login).setOnClickListener(mLoginListener);
        return mContentView;
    }

    private View.OnClickListener mLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            reLogin();
        }
    };

    protected void onLoginSuccess() {
        mNoLogin.setVisibility(View.GONE);
    }

    LabAsyncHttpResponseHandler responseHandler = new LabAsyncHttpResponseHandler() {
        @Override
        public void onFailure(LabResponse response, Object data) {
            LogHelper.e("omg","refresh << failed");
            mRefresh.setRefreshing(false);
            if (response != null && !TextUtils.isEmpty(response.msg)) {
                MessageUtils.showToast(response.msg);
            }
        }

        @Override
        public void onSuccess(LabResponse response, Object data) {
            LogHelper.e("omg","refresh << onSuccess");
            mRefresh.setRefreshing(false);
            if (data != null) {
                try {
                    mOrderDatas = JSON.parseArray(data.toString(), OrderItem.class);
                    if (mAdapter.getData() != null) {
                        mAdapter.getData().clear();
                        mAdapter.getData().addAll(mOrderDatas);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.setData(mOrderDatas);
                        mOrderList.setEmptyView(mEmpty);
                        mOrderList.setAdapter(mAdapter);
                    }
                } catch (Exception e) {
                    mEmpty.setVisibility(View.VISIBLE);
                }
            } else {
                mEmpty.setVisibility(View.VISIBLE);
            }
        }
    };


    LabAsyncHttpResponseHandler loaderMoreResponse = new LabAsyncHttpResponseHandler() {
        @Override
        public void onFailure(LabResponse response, Object data) {
            mRefresh.setRefreshing(false);
            if (response != null && !TextUtils.isEmpty(response.msg)) {
                MessageUtils.showToast(response.msg);
            }
        }

        @Override
        public void onSuccess(LabResponse response, Object data) {
            mRefresh.setRefreshing(false);
            if (data != null) {
                try {
                    mOrderDatas = JSON.parseArray(data.toString(), OrderItem.class);
                    if (mAdapter.getData() != null) {
                        mAdapter.getData().addAll(mOrderDatas);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.setData(mOrderDatas);
                        mOrderList.setEmptyView(mEmpty);
                        mOrderList.setAdapter(mAdapter);
                    }
                } catch (Exception e) {
                    mEmpty.setVisibility(View.VISIBLE);
                }
            } else {
                mEmpty.setVisibility(View.VISIBLE);
            }
        }
    };

    public int getSize() {
        return mAdapter == null ? 0 : (mAdapter.getCount());
    }

    private void getOrderList() {
        LogHelper.e("omg","getOrderList");
        if (mRefresh != null ) {
            mRefresh.setRefreshing(true);
            LogHelper.e("omg", "OrderBusiness.getOrderList");
            OrderBusiness.getOrderList(getActivity(), mAsyncHttpClient, responseHandler, mType, 0, DEFUALT_SIZE);
        }
    }

    private void loadMore() {
        if (mRefresh != null && !mRefresh.isRefreshing()) {
            mRefresh.setRefreshing(true);
            LogHelper.e("omg", "load more " + getSize() + "|" + (getSize() + DEFUALT_SIZE));
            OrderBusiness.getOrderList(getActivity(), mAsyncHttpClient, loaderMoreResponse, mType, getSize(),  DEFUALT_SIZE);
        }
    }

    @Override
    public void onRefresh() {
        getOrderList();
    }

    //新的push广播消息
    private BroadcastReceiver mOrderStatusChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getOrderList();
        }
    };

    class OrderAdapter extends BaseListAdapter<OrderItem> {

        public OrderAdapter(Context context, int resourceId) {
            super(context, resourceId);
        }

        @Override
        protected ViewHolder view2Holder(View view) {
            MessageHolder holder = new MessageHolder();
            holder.mStatus = (TextView) view.findViewById(R.id.order_status);
            holder.mService = (TextView) view.findViewById(R.id.service_name);
            holder.mTime = (TextView) view.findViewById(R.id.order_time);
            holder.mImage = (ImageView) view.findViewById(R.id.author_img);
            holder.mAddress = (TextView) view.findViewById(R.id.order_address);
            return holder;
        }

        @Override
        protected void bindView(ViewHolder holder, OrderItem item, int position) {
            MessageHolder OrderHolder = (MessageHolder) holder;
            OrderHolder.mStatus.setText(item.getStatusContent());
            OrderHolder.mStatus.setEnabled(!item.isClosed());
            String time = item.getServiceDate();
            if (time != null && time.indexOf(" ") > 1) {
                OrderHolder.mTime.setText(time.substring(0, time.indexOf(" ")));
            } else {
                OrderHolder.mTime.setText(item.getServiceDate());
            }

            OrderHolder.mService.setText(item.getServiceName());
            OrderHolder.mAddress.setText(item.getServiceAddress());
            ImageHelper.displayCtImage(item.getHeadPic(), OrderHolder.mImage, null);
        }
    }

    class MessageHolder extends ViewHolder {
        public ImageView mImage;
        public TextView mStatus;
        public TextView mService;
        public TextView mTime;
        public TextView mAddress;
    }
}
