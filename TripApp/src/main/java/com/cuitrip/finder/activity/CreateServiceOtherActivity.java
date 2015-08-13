package com.cuitrip.finder.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.app.BrowserActivity;
import com.lab.location.LocationHelper;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.lab.utils.imageupload.URLImageParser;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/26.
 */
public class CreateServiceOtherActivity extends BaseActivity implements View.OnClickListener {
    public static final int MAX_TAG_SIZE = 3;
    String[] times = null;
    String[] persons = null;
    String[] orderPriceType = null;
    //    final String[] countries = new String[]{"中国", "中国台湾"};
//    final String[] countryValues = new String[]{"CN", "TW"};
    final String[] priceType = new String[]{"TWD", "CNY"};

    private AsyncHttpClient mClient = new AsyncHttpClient();
    private ServiceInfo mServiceInfo;

    private TextView mTag;
    private TextView mMeet;
    private TextView mPriceDesc;
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
    private View container;
//    private String countryValue;


    public List<String> selectedTags = new ArrayList<>();

    public String[] getTimes() {
        if (times == null) {
            times = getResources().getStringArray(R.array.ct_service_times_list);
        }
        return times;
    }

    public String[] getPersons() {
        if (persons == null) {
            persons = getResources().getStringArray(R.array.ct_service_people_list);
        }
        return persons;
    }

    public String[] getOrderPriceType() {
        if (orderPriceType == null) {
            orderPriceType = getResources().getStringArray(R.array.ct_service_pay_list);
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
        container = findViewById(R.id.create_order_v);
        mTag = (TextView) findViewById(R.id.ct_tag_tv);
        mMeet = (TextView) findViewById(R.id.service_meet);
        mPriceDesc = (TextView) findViewById(R.id.price_desc_tv);
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
        mTag.setText(mServiceInfo.getTags());
        mMeet.setTag(mServiceInfo.getMeetLocation());
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(mServiceInfo.getPriceInclude())) {
            stringBuilder.append(mServiceInfo.getPriceInclude());
        }
        if (!TextUtils.isEmpty(mServiceInfo.getPriceUninclude())) {
            if (!TextUtils.isEmpty(stringBuilder.toString())) {
                stringBuilder.append(";");
            }
            stringBuilder.append(mServiceInfo.getPriceUninclude());
        }
        mPriceDesc.setText(stringBuilder.toString());

        if (mServiceInfo.getMaxbuyerNum() != null) {
            countValue = mServiceInfo.getMaxbuyerNum() + "";
            mCount.setText(countValue);
        }
//        if(mServiceInfo.getCountry() != null){
//            for(int i =0; i<countryValues.length; i++){
//                if(mServiceInfo.getCountry().equals(countryValues[i])){
//                    mCountry.setText(countries[i]);
//                }
//            }
//        }

        findViewById(R.id.ct_tag_block).setOnClickListener(this);
        findViewById(R.id.address_block).setOnClickListener(this);
        findViewById(R.id.time_block).setOnClickListener(this);
        findViewById(R.id.meet_block).setOnClickListener(this);
        findViewById(R.id.person_block).setOnClickListener(this);
        findViewById(R.id.pay_block).setOnClickListener(this);
        findViewById(R.id.price_desc_block).setOnClickListener(this);
        mPriceType.setOnClickListener(this);

//        findViewById(R.id.service_country).setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_menu_commit_service, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mTagsPopupWindow == null || !mTagsPopupWindow.isShowing()) {
            menu.findItem(R.id.action_service_commit).setTitle(R.string.ct_service_confirm);
        } else {
            menu.findItem(R.id.action_service_commit).setTitle(R.string.ct_confirm);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_service_commit:
                if (mTagsPopupWindow == null || !mTagsPopupWindow.isShowing()) {
                    commitService();
                } else {
                    saveTags();
                }
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
        String desc = mServiceInfo.getDescpt();
        desc = URLImageParser.badReplae(desc);
        ServiceBusiness.commitServiceInfo(this, mClient, new LabAsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        hideNoCancelDialog();
                        startActivity(new Intent(CreateServiceOtherActivity.this, CreateServiceSuccessActivity.class));
                        setResult(RESULT_OK);
                        finish();
                        LogHelper.e("omg", "suc");
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        LogHelper.e("omg", "failed");
                        hideNoCancelDialog();
                        MessageUtils.showToast(response.msg);
                    }
                }, mServiceInfo.getSid(), mServiceInfo.getName(), mAddress.getText().toString().trim(), desc,
                mServiceInfo.getPic(), mServiceInfo.getBackPic(), paywayValue == 2 ? "0" : mMoney.getText().toString().trim(),
                countValue, timeValue, null, null, paywayValue, "TW", mPriceType.getText().toString().trim(),
                LocationHelper.getLoation());
        LocationHelper.closeLocation();
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder;
        switch (view.getId()) {

            case R.id.ct_tag_block:
                showTags();
                break;
            case R.id.address_block:
                break;
            case R.id.meet_block:
                break;
            case R.id.price_block:
                break;
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

    @Override
    public void onBackPressed() {
        if (mTagsPopupWindow != null && mTagsPopupWindow.isShowing()) {
            mTagsPopupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    PopupWindow mTagsPopupWindow;

    private int buttonHeight = Utils.dp2pixel(30);

    public void showTags() {
        tempTags = new ArrayList<>();
        tempTags.addAll(selectedTags);
        if (mTagsPopupWindow == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.ct_service_tags, null);
            LinearLayout tagsLayout = (LinearLayout) view.findViewById(R.id.ct_tags_layout);
            buildTagsView(tagsLayout);
            mTagsPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, MainApplication.getInstance().getPageHeight() - Utils.dp2pixel(48 + 25));
            mTagsPopupWindow.setOutsideTouchable(true);
            view.findViewById(R.id.empty_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTagsPopupWindow.dismiss();
                }
            });
            mTagsPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    supportInvalidateOptionsMenu();
                }
            });
        } else {
            for (TextView tv : tagTvs) {
                tv.setSelected(tempTags.contains(tv.getTag()));
            }
        }
        mTagsPopupWindow.showAtLocation(container, Gravity.TOP, 0, Utils.dp2pixel(48 + 25));
        supportInvalidateOptionsMenu();
    }

    public String[] tags = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    public List<String> tempTags = new ArrayList<>();
    public List<TextView> tagTvs = new ArrayList<>();
    public int widthSize = 3;

    public void buildTagsView(LinearLayout tagsLayout) {
        tagsLayout.clearFocus();
        LinearLayout wrapper = null;
        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            if (i % widthSize == 0) {
                if (wrapper != null) {
                    tagsLayout.addView(wrapper);
                }
                wrapper = initLineWrapper();
            }

            TextView categoryItem = initCategoryItem(tags[i], initInnerTagWidth());

            wrapper.addView(categoryItem);
            if (i == tags.length - 1) {
                if (wrapper != null) {
                    tagsLayout.addView(wrapper);
                }
            }
            categoryItem.setSelected(tempTags.contains(tag));
            tagTvs.add(categoryItem);
        }
    }

    private LinearLayout initLineWrapper() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = Utils.dp2pixel(5);
        params.setMargins(margin, Utils.dp2pixel(8), margin, margin);

        layout.setLayoutParams(params);

        // 第一行的时候需要加点空间
        return layout;
    }

    private LinearLayout.LayoutParams initInnerTagWidth() {
        int pageWidth = MainApplication.getInstance().getPageWidth();
        int outSideMargin = Utils.dp2pixel(5);
        int innerMargin = Utils.dp2pixel(5);
        int tagMargin = outSideMargin + innerMargin;
        int tagPerMargin = innerMargin + innerMargin;
        int tagWidth = (pageWidth - 2 * tagMargin - 2 * tagPerMargin) / 3;

        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(tagWidth, buttonHeight);
        int m = Utils.dp2pixel(4);
        innerParams.setMargins(m, 0, m, 0);
        return innerParams;
    }

    protected void saveTags() {
        selectedTags = tempTags;
        if (mTagsPopupWindow != null) {
            mTagsPopupWindow.dismiss();
        }
        setTagsText();
    }

    protected void setTagsText() {
        mTag.setText(TextUtils.join("|", selectedTags));
    }

    protected View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            if (tempTags.contains(tag)) {
                LogHelper.e("omg", "deselect " + tag);
                v.setSelected(false);
                tempTags.remove(tag);
            } else {
                if (tempTags.size() >= MAX_TAG_SIZE) {
                    MessageUtils.showToast(getString(R.string.max_tag_limit,MAX_TAG_SIZE));
                    return;
                }
                LogHelper.e("omg", "select " + tag);
                v.setSelected(true);
                tempTags.add(tag);
            }
        }
    };

    private TextView initCategoryItem(final String tag, LinearLayout.LayoutParams innerParams) {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(innerParams);
        textView.setText(tag);
        textView.setTag(tag);
        textView.setHeight(Utils.dp2pixel(30));
        textView.setBackgroundResource(R.drawable.select_alpha_blue_corner);
        textView.setTextColor(getResources().getColorStateList(R.color.ct_blue_white));
        textView.setOnClickListener(onClickListener);
        return textView;
    }
}
