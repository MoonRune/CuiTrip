package com.cuitrip.app;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.cuitrip.business.OrderBusiness;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.model.AvailableDate;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.ServiceDetail;
import com.cuitrip.model.ServiceInfo;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created on 7/20.
 */
public class ModifyOrderActivity extends BaseActivity implements View.OnClickListener {

    public static final String ORDER_KEY = "ModifyOrderActivity.ORDER_KEY";
    public static final int REQUEST_MODIFY = 33;
    @InjectView(R.id.ct_service_name_tv)
    TextView ctServiceNameTv;
    @InjectView(R.id.selected_date)
    TextView mDate;
    @InjectView(R.id.selected_count)
    TextView mCount;
    @InjectView(R.id.ct_order_price_tv)
    TextView ctOrderPriceTv;
    private OrderItem mOrder;
    private ServiceInfo mService;

    private AlertDialog mSelectDialog;
    private ListView mSelectListView;
    private SelectBaseAdapter mAdapter;

    private List<String> mPersons;
    private List<String> mAvailableDate;

    private AsyncHttpClient mClient = new AsyncHttpClient();

    public static void startModify(Activity activity, OrderItem orderItem) {
        LogHelper.e("startModify", com.alibaba.fastjson.JSONObject.toJSONString(orderItem));
        activity.startActivityForResult(new Intent(activity, ModifyOrderActivity.class)
                .putExtra(ORDER_KEY, orderItem), REQUEST_MODIFY);

    }

    public static boolean isModifyed(int requestCode, int resultcode, Intent bundle) {
        return requestCode == REQUEST_MODIFY && resultcode == RESULT_OK;

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mOrder = (OrderItem) intent.getSerializableExtra(ORDER_KEY);
        if (mOrder == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(R.string.ct_order_modify);
        showLoading();
        ServiceBusiness.getServiceDetail(this, mClient, new LabAsyncHttpResponseHandler(ServiceDetail.class) {

            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideLoading();
                LogHelper.e("omg", " suc result " + response.result);
                if (data != null) {
                    ServiceDetail detail = (ServiceDetail) data;
                    mService = detail.getServiceInfo();
                    intView();
                } else {
                    MessageUtils.showToast(R.string.network_data_error);
                    finish();
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideLoading();
                LogHelper.e("omg", " suc failed " + response.result);
                if (!TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                }

            }
        }, mOrder.getSid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_modify_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_OK:
                modifyOrder();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void intView() {
        setContentView(R.layout.ct_modify_order);
        ButterKnife.inject(this);
        mDate = (TextView) findViewById(R.id.selected_date);
        mCount = (TextView) findViewById(R.id.selected_count);


        findViewById(R.id.order_date).setOnClickListener(this);
        findViewById(R.id.order_person).setOnClickListener(this);

        ctServiceNameTv.setText(mService.getName());
//        setViewText(R.id.create_order, getString(R.string.ct_order_modify_confirm));
        mDate.setText(Utils.getMsToD(mOrder.getServiceDate()));
        mCount.setText(mOrder.getBuyerNum());
        ctOrderPriceTv.setText(mOrder.getPayCurrency() + " " + mOrder.getOrderPrice());
        mSelectListView = (ListView) View.inflate(this, R.layout.ct_choice_list, null);
        mSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectDialog.dismiss();
                if (mAdapter.isDate()) {
                    mDate.setText(mAdapter.getData().get(i));
                } else {
                    mCount.setText(mAdapter.getData().get(i));
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
        }, mOrder.getSid());
    }

    protected void onLoginSuccess() {
        modifyOrder();
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
        //TODO
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

    private void modifyOrder() {
        //TODO
        if (mDate.getText().equals(getString(R.string.ct_order_select_date))) {
            MessageUtils.showToast(R.string.ct_create_order_null_date);
            return;
        } else if (mCount.getText().equals(getString(R.string.ct_order_select_person_count))) {
            MessageUtils.showToast(R.string.ct_create_order_null_person);
            return;
        }
        showNoCancelDialog();
        OrderBusiness.modifyOrder(this, mClient, new LabAsyncHttpResponseHandler(OrderItem.class) {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        hideNoCancelDialog();
                        setResult(RESULT_OK);
                        MessageUtils.showToast(getString(R.string.ct_modify_only_suc));
                        finish();
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        hideNoCancelDialog();
                        MessageUtils.showToast(response.msg);
                    }
                }, mOrder.getOid(), mOrder.getSid(), mService.getName(),
                Utils.parseStringToLongTime(mDate.getText().toString(), Utils.DATE_FORMAT_DAY),
                mCount.getText().toString(), mService.getPrice(), mService.getMoneyType());
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
                view = View.inflate(ModifyOrderActivity.this, R.layout.ct_choice_item, null);
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
                if (mCount.getText().toString().equals(getItem(i))) {
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
