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

    AreaMode countrySelect = null;
    AreaMode preSelect = null;
    AreaMode citySelect = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_select);
        showActionBar(getString(R.string.count_select_title));
        ButterKnife.inject(this);
        listViews.add(countryList);
        listViews.add(preList);
        listViews.add(cityList);
        request(0, "");
        countryList.setAdapter(new LocationAdapter(0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag() instanceof AreaMode) {
                    countrySelect = (AreaMode) v.getTag();
                    LogHelper.e("omg", "unselect  onclick location id " + countrySelect.getLocationId());
                }
                LogHelper.e("omg", "select  onclick" + v);
                v.setSelected(true);
                request(1, ((AreaMode) v.getTag()).getAbbr());
                selectArea(0, ((AreaMode) v.getTag()));
                ((LocationAdapter) countryList.getAdapter()).notifyDataSetChanged();

            }
        }));
        preList.setAdapter(new LocationAdapter(1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getTag() != null && v.getTag() instanceof AreaMode) {
                    preSelect = (AreaMode) v.getTag();
                }

                v.setSelected(true);

                if (((LocationAdapter) preList.getAdapter()).hasLowerArea()) {
                    request(2, ((AreaMode) v.getTag()).getAbbr());
                }
                selectArea(1, ((AreaMode) v.getTag()));
                ((LocationAdapter) preList.getAdapter()).notifyDataSetChanged();

            }
        }));
        cityList.setAdapter(new LocationAdapter(2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag() instanceof AreaMode) {
                    citySelect = (AreaMode) v.getTag();
                }

                v.setSelected(true);

                selectArea(2, ((AreaMode) v.getTag()));
                ((LocationAdapter) cityList.getAdapter()).notifyDataSetChanged();

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
                LogHelper.e("omg", "data ！＝null" + (data != null) + "|" + data);
                if (data != null && data instanceof LocationMode) {
                    LogHelper.e("omg", "data instan of");
                    if (((LocationMode) data).getContent() == null || ((LocationMode) data).getContent().isEmpty()) {
                        LogHelper.e("omg", "data content empty" );
                        MessageUtils.showToast(getString(R.string.non_lower_areas));
                    }
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
                for (int x = listViews.size() - 1; x >= 0; x--) {
                    if (x > code) {
                        listViews.get(x).setVisibility(View.GONE);
                        ((LocationAdapter) listViews.get(x).getAdapter()).clear();

                    }
                    if (x == code) {
                        listViews.get(x).setVisibility(View.VISIBLE);
                        ((LocationAdapter) listViews.get(x).getAdapter()).clear();
                    }

                }
                MessageUtils.showToast(msg);
                hideNoCancelDialog();
            }
        }, UnitUtils.getLanguage(), location);
    }

    public class LocationAdapter extends BaseAdapter {
        LocationMode mode;
        View.OnClickListener onClickListener;
        int index;

        public boolean hasLowerArea() {
            if (mode != null) {
                int type = 0;
                try {
                    type = Integer.valueOf(mode.getLocationType());
                } catch (NumberFormatException e) {
                    return false;
                }
                return type < 2;
            }
            return false;
        }

        public LocationAdapter(int index, View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            this.index = index;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item, parent, false);
            AreaMode mode = ((AreaMode) getItem(position));
            if (mode != null) {
                TextView textView = ((TextView) view.findViewById(R.id.area));
                textView.setTag(mode);
                textView.setText(mode.getName());
                textView.setOnClickListener(onClickListener);
                AreaMode tempMode = null;
                switch (index) {
                    case 0:

                        tempMode = countrySelect;
                        break;
                    case 1:
                        tempMode = preSelect;
                        break;
                    case 2:
                        tempMode = citySelect;
                        break;
                }
                if (tempMode != null && mode.equals(tempMode)) {
                    textView.setSelected(true);
                    LogHelper.e("omg", "select  notification " + textView);
                } else {
                    textView.setSelected(false);
                }
            } else {
                ((TextView) view.findViewById(R.id.area)).setText(R.string.no_date);

            }
            return view;
        }
    }
}
