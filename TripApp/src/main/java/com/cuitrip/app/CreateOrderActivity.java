package com.cuitrip.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.cuitrip.app.rong.RongCloudEvent;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.AvailableDate;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;


/**
 * Created on 7/18.
 */
public class CreateOrderActivity extends BaseActivity implements View.OnClickListener {
    public static final String SERVICE_INFO = "ORDER_SERVICE_INFO";

    private ServiceInfo mService;

    private TextView mDate, mCount;
    private TextView mMoneyDesc, mMoney;

    private AlertDialog mSelectDialog;
    private ListView mSelectListView;
    private SelectBaseAdapter mAdapter;

    private List<String> mPersons;
    private List<String> mAvailableDate;

    private AsyncHttpClient mClient = new AsyncHttpClient();
    String selectedCount;

    public static void start(Context context, ServiceInfo serviceInfo) {

        context.startActivity(new Intent(context, CreateOrderActivity.class)
                .putExtra(SERVICE_INFO, serviceInfo));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_create_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_OK:
                createOrder();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mService = (ServiceInfo) intent.getSerializableExtra(SERVICE_INFO);
        if (mService == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(R.string.ct_order);
        setContentView(R.layout.ct_create_order);
        mDate = (TextView) findViewById(R.id.selected_date);
        mCount = (TextView) findViewById(R.id.selected_count);

        mMoneyDesc = (TextView) findViewById(R.id.money_desc);


        ((TextView) findViewById(R.id.ct_service_name_tv)).setText(mService.getName());


        mMoney = (TextView) findViewById(R.id.bill_count);

        try {

            if (mService.isTypeFree()) {
                mMoneyDesc.setText("");
                mMoney.setText(R.string.ct_service_free);
            } else {
                double value = Double.valueOf(mService.getShowPrice());
                if (value <= 0) {
                    mMoneyDesc.setText("");
                    mMoney.setText(R.string.ct_service_free);
                } else {
                    if (mService.getPriceType() == 1) {
                        mMoney.setText(mService.getShowPrice() + getString(R.string.ct_service_unit));
                    } else {
                        mMoney.setText(mService.getShowPrice());
                    }
                    mMoneyDesc.setText(mService.getShowCurrency().toUpperCase());
                }
            }
        } catch (Exception e) {
            if (mService.getPriceType() == 1) {
                mMoney.setText(mService.getShowPrice() + getString(R.string.ct_service_unit));
            } else {
                mMoney.setText(mService.getShowPrice());
            }
            mMoneyDesc.setText(mService.getShowCurrency().toUpperCase());
        }


        findViewById(R.id.order_date).setOnClickListener(this);
        findViewById(R.id.order_person).setOnClickListener(this);

        mSelectListView = (ListView) View.inflate(this, R.layout.ct_choice_list, null);
        mSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectDialog.dismiss();
                if (mAdapter.isDate()) {
                    mDate.setText(mAdapter.getData().get(i));
                } else {
                    selectedCount = mAdapter.getData().get(i);
                    mCount.setText(getString(R.string.per_man_with_num_above, mAdapter.getData().get(i)));
                }
            }
        });
        mAdapter = new SelectBaseAdapter();
        mSelectListView.setAdapter(mAdapter);
        mSelectDialog = new AlertDialog.Builder(this, R.style.ctDialog).setCancelable(true)
                .create();
        mSelectDialog.setView(mSelectListView);
    }

    protected void onDestroy() {
        super.onDestroy();
        mClient.cancelAllRequests(true);
    }

    private void getAvailableDate() {
        showLoading();
        ServiceBusiness.getServiceAvailableDate(this, mClient, new LabAsyncHttpResponseHandler(AvailableDate.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideLoading();
                if (data != null) {
                    List<Long> availableDate = ((AvailableDate) data).getAvailableDate();
                    if (availableDate != null) {
                        mAvailableDate = new ArrayList<String>();
                        for (long time : availableDate) {
                            LogHelper.e("omg", "fetched date :" + String.valueOf(time));
                            mAvailableDate.add(Utils.parseLongTimeToString(time, Utils.DATE_FORMAT_DAY));
                        }
                    }
                    showSelectedDialog(SelectBaseAdapter.TYPE_DATE);
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideLoading();
                MessageUtils.showToast(response.msg);
            }
        }, mService.getSid());
    }

    protected void onLoginSuccess() {
        createOrder();
    }

    protected void onLoginFailed() {
        MessageUtils.showToast(R.string.ct_login_failed);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_date:
                if (mAvailableDate == null || mAvailableDate.isEmpty()) {
                    getAvailableDate();
                } else {
                    showSelectedDialog(SelectBaseAdapter.TYPE_DATE);
                }
                break;
            case R.id.order_person:
                showSelectedDialog(SelectBaseAdapter.TYPE_PERSON);
                break;
        }
    }

    void showSelectedDialog(int type) {
        if (type == SelectBaseAdapter.TYPE_PERSON) {
            if (mPersons == null) {
                mPersons = new ArrayList<>();
                for (int i = 1; i <= mService.getMaxbuyerNum(); i++) {
                    mPersons.add(String.valueOf(i));
                }
            }
            mAdapter.setData(mPersons, SelectBaseAdapter.TYPE_PERSON);
        } else {
            mAdapter.setData(mAvailableDate, SelectBaseAdapter.TYPE_DATE);
        }
        mSelectListView.setAdapter(mAdapter);
        mSelectDialog.show();
        Window window = mSelectDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (mAdapter.getCount() > 6) {
            lp.height = getResources().getDimensionPixelOffset(R.dimen.ct_dp_40) * 6;
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        window.setAttributes(lp);
    }

    private void createOrder() {
        if (mDate.getText().equals(getString(R.string.ct_order_select_date))) {
            MessageUtils.showToast(R.string.ct_create_order_null_date);
            return;
        } else if (mCount.getText().equals(getString(R.string.ct_order_select_person_count)) || TextUtils.isEmpty(selectedCount)) {
            MessageUtils.showToast(R.string.ct_create_order_null_person);
            return;
        }
        showNoCancelDialog();
        LogHelper.e("omg", "create order" + mService.getInsiderId() + "|" + mService.getSid() + "|" + mService.getName() + "|" +
                Utils.parseStringToLongTime(mDate.getText().toString(), Utils.DATE_FORMAT_DAY) + "|" +
                selectedCount + "|" + mService.getPrice() + "|" + mService.getMoneyType());
        OrderBusiness.createOrder(this, mClient, new LabAsyncHttpResponseHandler(OrderItem.class) {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        hideNoCancelDialog();
                        if (data != null) {
                            OrderItem item = (OrderItem) data;
                            item.setServicePIC(mService.getBackPic());
                            startActivity(new Intent(CreateOrderActivity.this, IndexActivity.class)
                                    .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.ORDER_TAB));
                            try {
                                final OrderItem orderItem = ((OrderItem) data);
                                String myid = "";
                                UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
                                if (userInfo != null) {
                                    myid = userInfo.getUid();
                                }
                                List<String> userIds = new ArrayList<>();
                                userIds.add(item.getInsiderId());
                                userIds.add(myid);
                                String title = orderItem.getOid();
                                RongCloudEvent.getInstance().startConversation(title, userIds, orderItem.getOid());

                            } catch (Exception e) {
                                MessageUtils.showToast(R.string.load_error);
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        hideNoCancelDialog();
                        MessageUtils.showToast(response.msg);
                    }
                }, mService.getInsiderId(), mService.getSid(), mService.getName(),
                Utils.parseStringToLongTime(mDate.getText().toString(), Utils.DATE_FORMAT_DAY),
                selectedCount, mService.getPrice(), mService.getMoneyType());
    }


    class SelectBaseAdapter extends BaseAdapter {

        public static final int TYPE_DATE = 1;
        public static final int TYPE_PERSON = 2;

        private int mType;
        private List<String> mData = new ArrayList<>();

        public void setData(List<String> items, int type) {
            mData.clear();
            if (items != null) {
                mData.addAll(items);
            }
            mType = type;
            notifyDataSetChanged();
        }

        public List<String> getData() {
            return mData;
        }

        public boolean isDate() {
            return mType == TYPE_DATE;
        }


        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int i) {
            if (i < 0 || i >= mData.size()) {
                return null;
            }
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ChoiceViewholder viewholder;
            if (null == view || !(view instanceof CheckedTextView)) {
                view = View.inflate(CreateOrderActivity.this, R.layout.ct_choice_item, null);
                viewholder = new ChoiceViewholder();
                viewholder.mText = (CheckedTextView) view.findViewById(R.id.checktext);
                view.setTag(viewholder);
            } else {
                viewholder = (ChoiceViewholder) view.getTag();
            }
            viewholder.mText.setText(getItem(i));
            if (isDate()) {
                if (mDate.getText().toString().equals(getItem(i))) {
                    viewholder.mText.setChecked(true);
                } else {
                    viewholder.mText.setChecked(false);
                }
            } else {
                if (selectedCount != null && selectedCount.equals(getItem(i))) {
                    viewholder.mText.setChecked(true);
                } else {
                    viewholder.mText.setChecked(false);
                }
            }
            return view;
        }
    }

    class ChoiceViewholder {
        public CheckedTextView mText;
    }
}
