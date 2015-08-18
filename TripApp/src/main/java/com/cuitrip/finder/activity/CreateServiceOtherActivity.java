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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.PriceDescActivity;
import com.cuitrip.app.base.UnitUtils;
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
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Created on 7/26.
 */
public class CreateServiceOtherActivity extends BaseActivity implements View.OnClickListener {
    public static final int MAX_TAG_SIZE = 4;
    String[] times = null;
    String[] persons = null;
    String[] orderPriceType = null;
    //    final String[] countries = new String[]{"中国", "中国台湾"};
//    final String[] countryValues = new String[]{"CN", "TW"};
    final String[] priceType = new String[]{"TWD", "CNY"};
    private PricePaywayPop priceTypePop = new PricePaywayPop();
    private TagsPoop tagsPop = new TagsPoop();
    private ActvityPop[] actvityPops = {priceTypePop, tagsPop};
    public ArrayList tags;
    boolean isFetchingTags = false;
    ArrayList addresses = null;
    boolean isFetchingAddress = false;

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
        tryFetchTags();
        tryFetchAddress();
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

        refreshPrice();
        revertTags();
        mTag.setText(mServiceInfo.getTag());
        mMeet.setTag(mServiceInfo.getMeetLocation());
        refreshPriceDes();

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
        for (ActvityPop actvityPop : actvityPops) {
            String text = actvityPop.getMenuText();
            if (!TextUtils.isEmpty(text)) {
                menu.findItem(R.id.action_service_commit).setTitle(text);
                return super.onPrepareOptionsMenu(menu);
            }
        }
        menu.findItem(R.id.action_service_commit).setTitle(R.string.ct_service_confirm);
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_service_commit:
                for (ActvityPop actvityPop : actvityPops) {
                    if (actvityPop.onClick()) {
                        return true;
                    }
                }

                tryCommitService();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void tryCommitService() {
        MessageUtils.createHoloBuilder(this).setMessage(R.string.commit_service_content).setTitle(R.string.commit_service_title)
                .setPositiveButton(R.string.ct_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        commitService();
                    }
                })
                .setNegativeButton(R.string.ct_cancel, null)
                .show();
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
                LocationHelper.getLoation(),
                mMeet.getText().toString().trim(),
                mTag.getText().toString().trim(),
                mServiceInfo.getPriceInclude(),
                mServiceInfo.getPriceUninclude(),
                mServiceInfo.getLat(),
                mServiceInfo.getLng()
                );
        LocationHelper.closeLocation();
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder;
        switch (view.getId()) {

            case R.id.ct_tag_block:
                tagsPop.showTags();
                break;
            case R.id.address_block: {
                if (addresses == null) {
                    MessageUtils.showToast(getString(R.string.fetching_data));
                    return;
                }
                builder = MessageUtils.createHoloBuilder(this);
                builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
                        addresses), new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i < addresses.size()) {
                            mAddress.setText(String.valueOf(i + 1));
                            mServiceInfo.setAddress(String.valueOf(i + 1));
                        }
                    }
                });
                AlertDialog dialog = builder.show();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.height = getResources().getDimensionPixelOffset(R.dimen.ct_dp_240); // 高度
                window.setAttributes(lp);
            }
            break;
            case R.id.meet_block:
                break;
            case R.id.price_desc_block:
                String include = mServiceInfo == null ? "" : mServiceInfo.getPriceInclude();
                String uninclude = mServiceInfo == null ? "" : mServiceInfo.getPriceUninclude();
                PriceDescActivity.start(this, include, uninclude);
                break;
            case R.id.time_block: {
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
            }
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
                priceTypePop.showPriceType();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PriceDescActivity.isModifyed(requestCode, resultCode, data)) {
            mServiceInfo.setPriceInclude(PriceDescActivity.getInclude(data));
            mServiceInfo.setPriceUninclude(PriceDescActivity.getUninclude(data));
            refreshPriceDes();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshPrice() {
        if (mServiceInfo.getPriceType() == null) {
            removeView(R.id.price_block);
            removeView(R.id.service_price_type_unit);
            removeView(R.id.price_block_divider);
        } else {
            int value = mServiceInfo.getPriceType();
            paywayValue = mServiceInfo.getPriceType();
            mPayType.setText(getOrderPriceType()[paywayValue]);
            showView(R.id.price_block_divider);
            switch (value) {
                case ServiceInfo.PAYWAY_ALL:
                    showView(R.id.price_block);
                    removeView(R.id.service_price_type_unit);
                    if (!TextUtils.isEmpty(mServiceInfo.getMoneyType())) {
                        setViewText(R.id.service_price_type, mServiceInfo.getMoneyType());
                    }
                    if (!TextUtils.isEmpty(mServiceInfo.getPrice())) {
                        setViewText(R.id.service_price, mServiceInfo.getPrice());
                    }
                    break;
                case ServiceInfo.PAYWAY_FREE:
                    removeView(R.id.price_block);
                    removeView(R.id.service_price_type_unit);

                    break;
                case ServiceInfo.PAYWAY_PER:
                    showView(R.id.price_block);
                    showView(R.id.service_price_type_unit);
                    if (!TextUtils.isEmpty(mServiceInfo.getMoneyType())) {
                        setViewText(R.id.service_price_type, mServiceInfo.getMoneyType());
                    }
                    if (!TextUtils.isEmpty(mServiceInfo.getPrice())) {
                        setViewText(R.id.service_price, mServiceInfo.getPrice());
                    }
                    break;
            }
            if (mServiceInfo.getPriceType() == 1) {
                showView(R.id.service_price_type_unit);
            }
        }

    }

    public void refreshPriceDes() {

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

    }

    @Override
    public void onBackPressed() {
        for (ActvityPop actvityPop : actvityPops) {
            if (actvityPop.dismiss()) {
                return;
            }
        }
        super.onBackPressed();
    }

    public interface ActvityPop {
        boolean dismiss();

        String getMenuText();

        boolean onClick();
    }

    public void tryFetchTags() {
        if (tags == null && !isFetchingTags) {
            fetchTags();
        }
    }


    public void revertTags() {
        if (mServiceInfo != null && !TextUtils.isEmpty(mServiceInfo.getTag())) {
            String[] tags = (mServiceInfo.getTag()).split("/");
            if (tags != null) {
                selectedTags = new ArrayList<>();
                for (String tag : tags) {
                    selectedTags.add(tag);
                }
            }
        }
    }
    public void fetchTags() {
        isFetchingTags = true;
        ServiceBusiness.getServiceTags(this, mClient, new LabAsyncHttpResponseHandler(ArrayList.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {

                if (data != null && data instanceof ArrayList) {
                    tags = ((ArrayList) data);
                }
                if (tagsPop != null) {
                    tagsPop.build();
                }
                isFetchingTags = false;

            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                isFetchingTags = false;

            }
        }, UnitUtils.getLanguage());
    }

    public void tryFetchAddress() {
        if (addresses == null && !isFetchingAddress) {
            fetchAddress();
        }
    }

    public void fetchAddress() {
        isFetchingTags = true;
        ServiceBusiness.getCountryCity(this, mClient, new LabAsyncHttpResponseHandler(ArrayList.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {

                if (data != null && data instanceof ArrayList) {
                    addresses = ((ArrayList) data);
                }
                isFetchingAddress = false;

            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                isFetchingAddress = false;

            }
        }, UnitUtils.getLanguage(), UnitUtils.getCreateServiceCountry());
    }

    public class PricePaywayPop implements ActvityPop {

        PopupWindow mPriceTypeWindow;
        @InjectView(R.id.ct_price_payway_free_rb)
        RadioButton ctPriceTypeFreeRb;
        @InjectView(R.id.ct_price_payway_all_rb)
        RadioButton ctPriceTypeAllRb;
        @InjectView(R.id.ct_price_payway_per_rb)
        RadioButton ctPriceTypePerRb;
        @InjectView(R.id.empty_view)
        View emptyView;
        @InjectViews({R.id.ct_price_payway_all_rb, R.id.ct_price_payway_per_rb, R.id.ct_price_payway_free_rb})
        RadioButton[] checks;
        @InjectView(R.id.ct_price_payway_rg)
        RadioGroup ctPricePaywayRg;

        public void showPriceType() {
            if (mPriceTypeWindow == null) {
                View view = LayoutInflater.from(CreateServiceOtherActivity.this).inflate(R.layout.ct_service_price_type, null);
                ButterKnife.inject(this, view);
                mPriceTypeWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, MainApplication.getInstance().getPageHeight() - Utils.dp2pixel(48 + 25));
                mPriceTypeWindow.setOutsideTouchable(true);
                refreshCheck();
                view.findViewById(R.id.empty_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPriceTypeWindow.dismiss();
                    }
                });
                mPriceTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        supportInvalidateOptionsMenu();
                    }
                });
            } else {
                refreshCheck();
            }
            mPriceTypeWindow.showAtLocation(container, Gravity.TOP, 0, Utils.dp2pixel(48 + 25));
            supportInvalidateOptionsMenu();
        }

        public void refreshCheck() {
            ctPriceTypeFreeRb.setChecked(false);
            ctPriceTypeAllRb.setChecked(false);
            ctPriceTypePerRb.setChecked(false);
            if (mServiceInfo.getPriceType() != null) {
                if (mServiceInfo.getPriceType() < checks.length && mServiceInfo.getPriceType() > 0) {
                    checks[mServiceInfo.getPriceType()].setChecked(true);
                }
            }
        }


        @Override
        public boolean dismiss() {
            if (mPriceTypeWindow != null && mPriceTypeWindow.isShowing()) {
                mPriceTypeWindow.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public String getMenuText() {
            if (mPriceTypeWindow != null && mPriceTypeWindow.isShowing()) {

                return getString(R.string.ct_confirm);
            }
            return "";
        }

        public boolean savePriceType() {
            switch (ctPricePaywayRg.getCheckedRadioButtonId()) {
                case R.id.ct_price_payway_free_rb:
                    mServiceInfo.setPriceType(ServiceInfo.PAYWAY_FREE);
                    refreshPrice();
                    return true;
                case R.id.ct_price_payway_all_rb:
                    mServiceInfo.setPriceType(ServiceInfo.PAYWAY_ALL);
                    refreshPrice();
                    return true;
                case R.id.ct_price_payway_per_rb:
                    mServiceInfo.setPriceType(ServiceInfo.PAYWAY_PER);
                    refreshPrice();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onClick() {
            if (mPriceTypeWindow != null && mPriceTypeWindow.isShowing()) {
                if (savePriceType()) {
                    mPriceTypeWindow.dismiss();
                }
                return true;
            }
            return false;
        }
    }

    public class TagsPoop implements ActvityPop {
        PopupWindow mTagsPopupWindow;
        View view;


        public void build() {
            if (tags != null && tags.isEmpty()) {
                view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
                LinearLayout tagsLayout = (LinearLayout) view.findViewById(R.id.ct_tags_layout);
                buildTagsView(tagsLayout);
            }
        }

        public void showTags() {
            tempTags = new ArrayList<>();
            tempTags.addAll(selectedTags);
            if (mTagsPopupWindow == null) {
                view = LayoutInflater.from(CreateServiceOtherActivity.this).inflate(R.layout.ct_service_tags, null);
                build();
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
                if (tagTvs == null) {
                    build();
                }
                if (tagTvs != null) {
                    for (TextView tv : tagTvs) {
                        tv.setSelected(tempTags.contains(tv.getTag()));
                    }
                }
            }
            mTagsPopupWindow.showAtLocation(container, Gravity.TOP, 0, Utils.dp2pixel(48 + 25));
            supportInvalidateOptionsMenu();
        }


        // tags view
        private int buttonHeight = Utils.dp2pixel(30);
        public List<String> tempTags = new ArrayList<>();
        public List<TextView> tagTvs = new ArrayList<>();
        public int widthSize = 3;

        public void buildTagsView(LinearLayout tagsLayout) {
            tagsLayout.clearFocus();
            LinearLayout wrapper = null;
            for (int i = 0; i < tags.size(); i++) {
                String tag = String.valueOf(tags.get(i));
                if (i % widthSize == 0) {
                    if (wrapper != null) {
                        tagsLayout.addView(wrapper);
                    }
                    wrapper = initLineWrapper();
                }

                TextView categoryItem = initCategoryItem(String.valueOf(tags.get(i)), initInnerTagWidth());

                wrapper.addView(categoryItem);
                if (i == tags.size() - 1) {
                    if (wrapper != null) {
                        tagsLayout.addView(wrapper);
                    }
                }
                categoryItem.setSelected(tempTags.contains(tag));
                tagTvs.add(categoryItem);
            }
        }

        private LinearLayout initLineWrapper() {
            LinearLayout layout = new LinearLayout(CreateServiceOtherActivity.this);
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
            mTag.setText(TextUtils.join("/", selectedTags));
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
                        MessageUtils.showToast(getString(R.string.max_tag_limit, MAX_TAG_SIZE));
                        return;
                    }
                    LogHelper.e("omg", "select " + tag);
                    v.setSelected(true);
                    tempTags.add(tag);
                }
            }
        };

        private TextView initCategoryItem(final String tag, LinearLayout.LayoutParams innerParams) {
            TextView textView = new TextView(CreateServiceOtherActivity.this);
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

        @Override
        public boolean dismiss() {
            if (mTagsPopupWindow != null && mTagsPopupWindow.isShowing()) {
                mTagsPopupWindow.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public String getMenuText() {
            if (mTagsPopupWindow != null && mTagsPopupWindow.isShowing()) {
                return getString(R.string.ct_confirm);
            }
            return "";
        }

        @Override
        public boolean onClick() {
            if (mTagsPopupWindow != null && mTagsPopupWindow.isShowing()) {
                saveTags();
                mTagsPopupWindow.dismiss();
                return true;
            }
            return false;
        }
    }
}
