package com.cuitrip.app.bill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.model.BillData;
import com.cuitrip.model.Bills;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by baziii on 15/8/18.
 */
public class BillActivity extends BaseActivity {
    public static final String TAG = "BaseVerticalListActivity";
    @InjectView(R.id.ct_recycler_view)
    protected RecyclerView mRecyclerView;
    @InjectView(R.id.ct_swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager mLayoutManager;
    public static final int DEFAULT_SIZE = 20;
    @InjectView(R.id.desc)
    TextView desc;
    @InjectView(R.id.amount)
    TextView amount;
    //包括刷新和loadMore
    boolean loading = false;
    boolean hasMore = true;
    boolean isResumeRefresh = false;
    BillAdapter adapter = new BillAdapter();
    AsyncHttpClient mClient = new AsyncHttpClient();
    Bills bills;

    public static void start(Context context) {
        if (context != null) {
            context.startActivity(new Intent(context, BillActivity.class));
        }
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_bill, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_OK:
                if (bills != null) {
                    BillCashActivity.start(this, bills.getBalance(), bills.getMoneyType(), bills.getRate());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
        showActionBar(getString(R.string.bill_title));
        setContentView(R.layout.bill);
        ButterKnife.inject(this);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(onScrollListener);
        mRecyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        requestPresentRefresh();
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

    @Override
    protected void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void hideLoading() {
        isResumeRefresh = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void requestPresentRefresh() {
        showLoading();
        loading = true;
        OrderBusiness.getBills(this, mClient, new LabAsyncHttpResponseHandler(Bills.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                LogHelper.e(TAG, " suc" + String.valueOf(response.result));
                if (data != null && data instanceof Bills) {
                    bills = ((Bills) data);
                    LogHelper.e(TAG, " res " + String.valueOf(response.result));
                    amount.setText(getString(R.string.currency_money, bills.moneyType.toUpperCase(), bills.balance));
                    try {
                        desc.setText(getString(R.string.rate_desc, bills.rate, bills.moneyType.toUpperCase()));
                    } catch (Exception e) {
                        desc.setText(R.string.data_error);
                    }
                    adapter.setBillDatas(bills.getLists());
                    adapter.notifyDataSetChanged();
                    hideLoading();
                    loading = false;
                } else {
                    LogHelper.e(TAG, " not bills Data");
                    onFailure(response, data);
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                LogHelper.e(TAG, " failed");
                String msg;
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    msg = response.msg;
                } else {
                    msg = PlatformUtil.getInstance().getString(R.string.data_error);
                }
                MessageUtils.showToast(msg);
                hideLoading();
                loading = false;

            }
        }, UnitUtils.getSettingMoneyType(), 0, DEFAULT_SIZE);
    }

    public void requestPresentLoadMore() {
        showLoading();
        loading = true;
        int size = adapter.getItemCount();
        OrderBusiness.getBills(this, mClient, new LabAsyncHttpResponseHandler(Bills.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null && data instanceof Bills) {
                    bills = ((Bills) data);
                    LogHelper.e(TAG, " res " + String.valueOf(response.result));
                    amount.setText(getString(R.string.currency_money, bills.moneyType.toUpperCase(), bills.balance));
                    try {
                        desc.setText(getString(R.string.rate_desc, bills.rate, bills.moneyType.toUpperCase()));
                    } catch (Exception e) {
                        desc.setText(R.string.data_error);
                    }
                    adapter.getBillDatas().addAll(bills.getLists());
                    adapter.notifyDataSetChanged();
                    hideLoading();
                    loading = false;
                } else {
                    onFailure(response, data);
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                String msg;
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    msg = response.msg;
                } else {
                    msg = PlatformUtil.getInstance().getString(R.string.data_error);
                }
                MessageUtils.showToast(msg);
                hideLoading();
                loading = false;

            }
        }, UnitUtils.getMoneyType(), size, size + DEFAULT_SIZE);

    }

    public static class BillItemView extends RecyclerView.ViewHolder {

        @InjectView(R.id.image)
        CircleImageView image;
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.time)
        TextView time;
        @InjectView(R.id.amount)
        TextView amount;

        public BillItemView(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void render(BillData data) {
            LogHelper.e("render bills", data.toString());
            ImageHelper.displayPersonImage(data.getHeadPic(), image, null);
            name.setText(data.getTitle());
            time.setText(Utils.getMsToD(data.getGmtCreated()));

            try {
                double value = Double.valueOf(data.getMoney());
                DecimalFormat df = new DecimalFormat("#.00");
                if (value >= 0) {
                    amount.setTextColor(PlatformUtil.getInstance().getColor(R.color.ct_black));
                    amount.setText("+  " + getUpCasedMoneyType(data) + data.getMoney());
                } else {
                    amount.setTextColor(PlatformUtil.getInstance().getColor(R.color.ct_blue_pressed));
                    amount.setText("-  " + getUpCasedMoneyType(data) + data.getMoney());
                }
            } catch (NumberFormatException e) {
                amount.setTextColor(PlatformUtil.getInstance().getColor(R.color.ct_black));
                amount.setText(getUpCasedMoneyType(data) + data.getMoney());
            }
        }

        public String getUpCasedMoneyType(BillData data) {
            return (TextUtils.isEmpty(data.getMoneyType()) ? "" : data.getMoneyType().toUpperCase());
        }
    }

    public class BillAdapter extends RecyclerView.Adapter<BillItemView> {

        private List<BillData> billDatas = new ArrayList<>();

        public void setBillDatas(List<BillData> billDatas) {
            if (billDatas == null) {
                billDatas = new ArrayList<>();
            }
            this.billDatas = billDatas;
            notifyDataSetChanged();
        }

        public List<BillData> getBillDatas() {
            return billDatas;
        }

        @Override
        public BillItemView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item, null);
            BillItemView vh = new BillItemView(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(BillItemView holder, int position) {
            holder.render(billDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return billDatas == null ? 0 : billDatas.size();
        }
    }
}
