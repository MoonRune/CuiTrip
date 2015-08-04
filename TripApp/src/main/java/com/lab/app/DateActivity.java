package com.lab.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.TextView;

import com.cuitrip.app.ServiceDetailActivity;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.model.AvailableDate;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateActivity extends BaseActivity implements View.OnClickListener {

    private TextView mMonth;
    private GridView mDate;
    private Calendar mCalendar;
    private DateAdapter mAdapter;
    private AsyncHttpClient mClient = new AsyncHttpClient();
    private List<Long> mAvailableDate = new ArrayList<Long>();

    private boolean mIsFinder;
    private String mSid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showActionBar(R.string.ct_service_date_selected);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mSid = intent.getStringExtra(ServiceDetailActivity.SERVICE_ID);
        if (TextUtils.isEmpty(mSid)) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mIsFinder = intent.getIntExtra(ServiceDetailActivity.USER_TYPE, UserInfo.USER_TRAVEL)
                == UserInfo.USER_FINDER;

        setContentView(R.layout.ct_activity_date);
        if (mIsFinder) {
            setViewText(R.id.ct_booked, getString(R.string.ct_service_booked));
            setViewText(R.id.ct_can_cancel, getString(R.string.ct_service_cancelable));
            findViewById(R.id.finish).setOnClickListener(this);
            findViewById(R.id.finish).setVisibility(View.VISIBLE);
            setViewText(R.id.title, getString(R.string.ct_order_select_date));
        }
        findViewById(R.id.before_month).setOnClickListener(this);
        findViewById(R.id.after_month).setOnClickListener(this);
        mMonth = (TextView) findViewById(R.id.month_title);
        mCalendar = Calendar.getInstance();
        mMonth.setText(String.format(Locale.ENGLISH, getString(R.string.ct_service_date_title),
                mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1));

        mDate = (GridView) findViewById(R.id.date_week_grid);
        mAdapter = new DateAdapter();
        mAdapter.setCalendar(mCalendar);
        mDate.setAdapter(mAdapter);

        findViewById(R.id.head_top).setOnClickListener(this);
        findViewById(R.id.bottom_top).setOnClickListener(this);

        showLoading();
        ServiceBusiness.getServiceAvailableDate(this, mClient, new LabAsyncHttpResponseHandler(AvailableDate.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideLoading();
                if (data != null) {
                    mAvailableDate = ((AvailableDate) data).getAvailableDate();
                    mAdapter.setCalendar(mCalendar);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideLoading();
                if (response.code == 105 && mIsFinder) {//可约日期为空
                    return;
                }
                MessageUtils.showToast(response.msg);
            }
        }, mSid);
    }

    protected void onDestroy() {
        super.onDestroy();
        //mClient.cancelAllRequests(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.before_month:
                mCalendar.add(Calendar.MONTH, -1);
                mMonth.setText(String.format(Locale.ENGLISH, getString(R.string.ct_service_date_title),
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1));
                mAdapter.setCalendar(mCalendar);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.after_month:
                mCalendar.add(Calendar.MONTH, 1);
                mMonth.setText(String.format(Locale.ENGLISH, getString(R.string.ct_service_date_title),
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1));
                mAdapter.setCalendar(mCalendar);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.head_top:
            case R.id.bottom_top:
                finish();
                break;
            case R.id.finish:
                commitDateSet();
                break;
        }

    }

    public class DateAdapter extends BaseAdapter {

        private Calendar calendar = Calendar.getInstance();

        public void setCalendar(Calendar cal) {
            this.calendar.setTimeInMillis(cal.getTimeInMillis());
        }

        @Override
        public int getCount() {
            return 42; //按照最多6个星期计算
        }

        @Override
        public Integer getItem(int i) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return i + 2 - dayOfWeek;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null || !(view instanceof CheckedTextView)) {
                view = View.inflate(DateActivity.this, R.layout.ct_date_item, null);
            }
            CheckedTextView tv = (CheckedTextView) view;
            int date = getItem(position);
            if (date < 1 || date > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                tv.setText("");
                tv.setEnabled(false);
                tv.setVisibility(View.INVISIBLE);
            } else {
                calendar.set(Calendar.DAY_OF_MONTH, date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long start = calendar.getTimeInMillis();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                long end = calendar.getTimeInMillis();

                tv.setText(String.valueOf(date));
                boolean match = false;
                if (mAvailableDate != null && !mAvailableDate.isEmpty()) {
                    for (long time : mAvailableDate) {
                        if (time >= start && time < end) {
                            tv.setEnabled(true);
                            tv.setChecked(true);
                            match = true;
                            break;
                        }
                    }
                }
                if (!match) {
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);
                    if (mIsFinder) {
                        //当天之前不能被选中
                        if (start <= today.getTimeInMillis()) {
                            tv.setEnabled(false);
                            tv.setChecked(true);
                        } else {
                            tv.setEnabled(true);
                            tv.setChecked(false);
                        }
                    } else {
                        tv.setEnabled(false);
                        tv.setChecked(true);
                    }
                }
                view.setTag(Long.valueOf(start));
                tv.setVisibility(View.VISIBLE);
            }

            if (mIsFinder && view.isEnabled()) {
                view.setOnClickListener(onDateSelectedListener);
            }
            return view;
        }

        private View.OnClickListener onDateSelectedListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getTag() == null || !(view.getTag() instanceof Long)) {
                    return;
                }
                Long time = (Long) view.getTag();
                if (mAvailableDate.contains(time)) {
                    mAvailableDate.remove(time);
                    ((CheckedTextView) view).setChecked(false);
                } else {
                    mAvailableDate.add(time);
                    ((CheckedTextView) view).setChecked(true);
                }
            }
        };
    }

    private void commitDateSet() {
        showNoCancelDialog();
        ServiceBusiness.modifyServiceInfo(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideNoCancelDialog();
                MessageUtils.showToast(R.string.ct_service_date_setted);
                finish();
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideNoCancelDialog();
                MessageUtils.showToast(response.msg);

            }
        }, mSid, mAvailableDate);
    }
}
