package com.cuitrip.finder.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.app.BrowserActivity;
import com.lab.location.LocationHelper;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created on 7/26.
 */
public class CreateServiceOtherActivity extends BaseActivity implements View.OnClickListener {
     String[] times = null;
     String[] persons = null;
    String[] orderPriceType = null;
//    final String[] countries = new String[]{"中国", "中国台湾"};
//    final String[] countryValues = new String[]{"CN", "TW"};
    final String[] priceType = new String[]{"TWD", "CNY"};

    private AsyncHttpClient mClient = new AsyncHttpClient();
    private ServiceInfo mServiceInfo;

    private TextView mTime;
    private TextView mAddress;
    private TextView mCount;
    private TextView mPayType;
    private TextView mMoney;
//    private TextView mCountry;
    private TextView mPriceType;

    private String timeValue;
    private String countValue;
    private Integer paywayValue;
//    private String countryValue;


    public String[] getTimes() {
        if (times == null) {
            times= getResources().getStringArray(R.array.ct_service_times_list);
        }
        return times;
    }

    public String[] getPersons() {
        if (persons == null) {
            persons= getResources().getStringArray(R.array.ct_service_people_list);
        }
        return persons;
    }

    public String[] getOrderPriceType() {
        if (orderPriceType == null) {
            orderPriceType= getResources().getStringArray(R.array.ct_service_pay_list);
        }
        return orderPriceType;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_add_service);
        Intent intent = getIntent();
        if (intent != null) {
            mServiceInfo = (ServiceInfo) intent.getSerializableExtra(CreateServiceActivity.SERVICE_INFO);
            if (mServiceInfo == null) {
                MessageUtils.showToast(R.string.parameter_error);
                finish();
                return;
            }
        } else {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        setContentView(R.layout.ct_create_service_other);
        mTime = (TextView) findViewById(R.id.service_duration);
        mAddress = (TextView) findViewById(R.id.service_address);
        mCount = (TextView) findViewById(R.id.service_persons);
        mPayType = (TextView) findViewById(R.id.service_pay_way);
        mMoney = (TextView) findViewById(R.id.service_price);
//        mCountry = (TextView) findViewById(R.id.service_country);
        mPriceType = (TextView) findViewById(R.id.service_price_type);

        if (!TextUtils.isEmpty(mServiceInfo.getServiceTime())) {
            timeValue = mServiceInfo.getServiceTime();
            mTime.setText(mServiceInfo.getServiceTime());
        }

        if (!TextUtils.isEmpty(mServiceInfo.getAddress())) {
            mAddress.setText(mServiceInfo.getAddress());
        }

        if (mServiceInfo.getPriceType() != null) {
            paywayValue = mServiceInfo.getPriceType();
            mPayType.setText(getOrderPriceType()[paywayValue]);
            if (mServiceInfo.getPriceType() == 0 || mServiceInfo.getPriceType() == 1) {
                if (!TextUtils.isEmpty(mServiceInfo.getMoneyType())) {
                    setViewText(R.id.service_price_type, mServiceInfo.getMoneyType());
                }
                if (!TextUtils.isEmpty(mServiceInfo.getPrice())) {
                    setViewText(R.id.service_price, mServiceInfo.getPrice());
                }
                if (mServiceInfo.getPriceType() == 1) {
                    showView(R.id.service_price_type_unit);
                }
                showView(R.id.price_block);
            }
        }

        if (mServiceInfo.getMaxbuyerNum() != null) {
            countValue = mServiceInfo.getMaxbuyerNum()+"";
            mCount.setText(countValue);
        }
//        if(mServiceInfo.getCountry() != null){
//            for(int i =0; i<countryValues.length; i++){
//                if(mServiceInfo.getCountry().equals(countryValues[i])){
//                    mCountry.setText(countries[i]);
//                }
//            }
//        }

        findViewById(R.id.time_block).setOnClickListener(this);
        findViewById(R.id.person_block).setOnClickListener(this);
        findViewById(R.id.pay_block).setOnClickListener(this);
        findViewById(R.id.pay_introduce).setOnClickListener(this);
        mPriceType.setOnClickListener(this);

//        findViewById(R.id.service_country).setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_menu_commit_service, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_service_commit:
                commitService();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void commitService() {
//        if ((TextUtils.isEmpty(countryValue))) {
//            MessageUtils.showToast(R.string.ct_country_selected_null);
//            return;
//        }

        if (TextUtils.isEmpty(mAddress.getText().toString().trim())) {
            MessageUtils.showToast(R.string.ct_service_address_null);
            return;
        }
        if (timeValue == null || timeValue.trim().equals("")) {
            MessageUtils.showToast(R.string.ct_service_time_selected);
            return;
        }

        if (countValue == null || countValue.trim().equals("")) {
            MessageUtils.showToast(R.string.ct_service_count_null);
            return;
        }
        if (paywayValue == null) {
            MessageUtils.showToast(R.string.ct_service_pay_way_selecte_hint);
            return;
        }
        if ((paywayValue == 0 || paywayValue == 1)
                && TextUtils.isEmpty(mMoney.getText().toString().trim())) {
            MessageUtils.showToast(R.string.ct_service_money_set);
            return;
        }

        showNoCancelDialog();
        ServiceBusiness.commitServiceInfo(this, mClient, new LabAsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        hideNoCancelDialog();
                        startActivity(new Intent(CreateServiceOtherActivity.this, CreateServiceSuccessActivity.class));
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        hideNoCancelDialog();
                            MessageUtils.showToast(response.msg);
                    }
                }, mServiceInfo.getSid(), mServiceInfo.getName(), mAddress.getText().toString().trim(), mServiceInfo.getDescpt(),
                mServiceInfo.getPic(), mServiceInfo.getBackPic(), paywayValue == 2 ? "0" : mMoney.getText().toString().trim(),
                countValue, timeValue, null, null, paywayValue, "TW", mPriceType.getText().toString().trim(),
                LocationHelper.getLoation());
        LocationHelper.closeLocation();
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder;
        switch (view.getId()) {
//            case R.id.address_block:
//                break;
            case R.id.time_block:
                builder = MessageUtils.createHoloBuilder(this);
                builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
                       getTimes()), new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i < getTimes().length) {
                            mTime.setText(String.valueOf(i + 1));
                            timeValue = String.valueOf(i + 1);
                        }
                    }
                });
                AlertDialog dialog = builder.show();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.height = getResources().getDimensionPixelOffset(R.dimen.ct_dp_240); // 高度
                window.setAttributes(lp);
                break;
            case R.id.person_block:
                builder = MessageUtils.createHoloBuilder(this);
                builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
                        getPersons()), new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i < getPersons().length) {
                            mCount.setText(getPersons()[i]);
                            countValue = String.valueOf(i + 1);
                        }
                    }
                });
                builder.create().show();
                break;
            case R.id.pay_block:
                builder = MessageUtils.createHoloBuilder(this);
                final String[] payways = getOrderPriceType();
                builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
                        payways), new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i < payways.length) {
                            mPayType.setText(payways[i]);
                            paywayValue = i;
                            if (i == 0) {
                                showView(R.id.price_block);
                                removeView(R.id.service_price_type_unit);
                            } else if (i == 1) {
                                showView(R.id.price_block);
                                showView(R.id.service_price_type_unit);
                            } else {
                                removeView(R.id.price_block);
                            }
                        }
                    }
                });
                builder.create().show();
                break;
            case R.id.pay_introduce:
                startActivity(new Intent(this, BrowserActivity.class)
                        .putExtra(BrowserActivity.DATA, "file:///android_asset/html_bill.html")
                        .putExtra(BrowserActivity.TITLE, getString(R.string.ct_service_bill)));
                break;
            case R.id.service_price_type:
                builder = MessageUtils.createHoloBuilder(this);
                builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
                        priceType), new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i < priceType.length) {
                            mPriceType.setText(priceType[i]);
                        }
                    }
                });
                builder.create().show();
                break;
//            case R.id.service_country:
//                builder = MessageUtils.createHoloBuilder(this);
//                builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
//                        countries), new Dialog.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (i < countries.length) {
//                            mCountry.setText(countries[i]);
//                            countryValue = countryValues[i];
//                        }
//                    }
//                });
//                builder.create().show();
//                break;
        }

    }
}
