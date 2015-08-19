package com.cuitrip.app.country;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/19.
 */
public class CountrySelectActivity extends BaseActivity {
    public static final String VALUE = "VALUE";
    public static final int REQUEST = 5;
    @InjectView(R.id.selected)
    TextView selected;
    @InjectView(R.id.container)
    LinearLayout container;

    public static void start(Activity activity) {
        activity.startActivityForResult(new Intent(activity, CountrySelectActivity.class), REQUEST);
    }

    public static boolean isWrited(int req, int res, Intent data) {
        return req == REQUEST && res == RESULT_OK;
    }

    public static String getValue(Intent date) {
        return date.getStringExtra(VALUE);
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    ArrayList<ListView> listViews = new ArrayList<>();
    ArrayList<String> valus = new ArrayList<>();
    @InjectView(R.id.country_list)
    ListView countryList;
    @InjectView(R.id.pre_list)
    ListView preList;
    @InjectView(R.id.city_list)
    ListView cityList;

    public void renderTv() {
        selected.setText(TextUtils.join("-", valus));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_profile_submit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void save() {
        setResult(RESULT_OK, new Intent().putExtra(VALUE, TextUtils.join("-", valus)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_select);
        showActionBar("选择国家地区");
        ButterKnife.inject(this);
        listViews.add(countryList);
        listViews.add(preList);
        listViews.add(cityList);
        request(0, "");
        countryList.setAdapter(new LocationAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(1, ((AreaMode) v.getTag()).getAbbr());
                selectArea(0, ((AreaMode) v.getTag()));

            }
        }));
        preList.setAdapter(new LocationAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(2, ((AreaMode) v.getTag()).getAbbr());
                selectArea(1, ((AreaMode) v.getTag()));

            }
        }));
        cityList.setAdapter(new LocationAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectArea(2, ((AreaMode) v.getTag()));

            }
        }));
    }

    public void selectArea(int index, AreaMode mode) {
        for (int x = listViews.size() - 1; x >= index; x--) {
            if (x < valus.size()) {
                valus.remove(x);
            }
        }
        valus.add(mode.getName());
        renderTv();
    }

    public void request(final int code, String location) {
        LogHelper.e("omg", "request" + code + "|" + location);
        showNoCancelDialog();
        ServiceBusiness.getCountryCity(this, asyncHttpClient, new LabAsyncHttpResponseHandler(LocationMode.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                LogHelper.e("omg", "request" + response.result);
                if (data != null && data instanceof LocationMode) {
                    for (int x = listViews.size() - 1; x >= 0; x--) {
                        if (x > code) {
                            listViews.get(x).setVisibility(View.GONE);
                            ((LocationAdapter) listViews.get(x).getAdapter()).clear();

                        }
                        if (x == code) {
                            listViews.get(x).setVisibility(View.VISIBLE);
                            ((LocationAdapter) listViews.get(x).getAdapter()).set(((LocationMode) data));
                        }

                    }
                } else {
                    String msg = PlatformUtil.getInstance().getString(R.string.data_error);
                    MessageUtils.showToast(msg);
                }
                hideNoCancelDialog();
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
                hideNoCancelDialog();
            }
        }, UnitUtils.getLanguage(), location);
    }

    public static class LocationAdapter extends BaseAdapter {
        LocationMode mode;
        View.OnClickListener onClickListener;

        public LocationAdapter(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void set(LocationMode mode) {
            this.mode = mode;
            notifyDataSetChanged();
        }

        public void clear() {
            mode = null;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mode == null ? 1 : (mode.getContent() == null ? 1 : mode.getContent().size());
        }

        @Override
        public Object getItem(int position) {
            if (mode == null || mode.getContent() == null) {
                return null;
            }
            return mode.getContent().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item, null);
            AreaMode mode = ((AreaMode) getItem(position));
            if (mode != null) {
                ((TextView) view.findViewById(R.id.area)).setTag(mode);
                ((TextView) view.findViewById(R.id.area)).setText(mode.getName());
                ((TextView) view.findViewById(R.id.area)).setOnClickListener(onClickListener);
            } else {
                ((TextView) view.findViewById(R.id.area)).setText("无数据");

            }
            return view;
        }
    }
}
