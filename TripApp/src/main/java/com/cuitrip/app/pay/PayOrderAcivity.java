package com.cuitrip.app.pay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.OrderFragment;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.DiscountItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.pingplusplus.android.PaymentActivity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/15.
 * <p/>
 * 好像用databinding啊啊啊啊啊啊 啊
 */
public class PayOrderAcivity extends BaseActivity implements IPayOrderView {

    public static final String TAG = "PayOrderAcivity";
    public static final String ORDER_ID_KEY = "PayOrderAcivity.ORDER_ID_KEY";
    public static final int REQUEST_FOR_PAY = 1000;
    public static final int RESULT_PAY_SUC = 1001;
    public static final int RESULT_PAY_FAILED = 1010;

    PayOrderPresent payOrderPresent;
    @InjectView(R.id.ct_service_ava)
    ImageView ctServiceAva;
    @InjectView(R.id.ct_service_name_tv)
    TextView ctServiceNameTv;
    @InjectView(R.id.ct_order_date_buyer_size_tv)
    TextView ctOrderDateBuyerSizeTv;
    @InjectView(R.id.ct_order_price_whith_currency_tv)
    TextView ctOrderPriceWhithCurrencyTv;
    @InjectView(R.id.ct_order_discounts_tv)
    TextView ctOrderDiscountsTv;
    @InjectView(R.id.ct_order_discounts_ll)
    View ctOrderDiscountsll;
    @InjectView(R.id.ct_order_discount_content_tv)
    TextView ctOrderDiscountContentTv;
    @InjectView(R.id.ct_order_normal_price_with_currency_tv)
    TextView ctOrderNormalPriceWithCurrencyTv;
    @InjectView(R.id.ct_order_discount)
    TextView ctOrderDiscount;
    @InjectView(R.id.ct_order_final_price_with_currency)
    TextView ctOrderFinalPriceWithCurrency;
    @InjectView(R.id.container)
    LinearLayout container;
    PayMethodPop payMethodPop = new PayMethodPop();
    DiscountSelectPop discountSelectPop = new DiscountSelectPop();
    public static void startActivity(Activity context, String orderid) {
        Intent intent = new Intent(context, PayOrderAcivity.class);
        intent.putExtra(ORDER_ID_KEY, orderid);
        context.startActivityForResult(intent, REQUEST_FOR_PAY);
    }

    public static boolean isPaySUc(int requestCode, int resultCode, Intent data) {
        return requestCode == REQUEST_FOR_PAY && resultCode == RESULT_PAY_SUC;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(getString(R.string.ct_pay_order_title_default));
        setContentView(R.layout.ct_pay_order);
        ButterKnife.inject(this);
        String oid = getIntent().getStringExtra(ORDER_ID_KEY);
        payOrderPresent = new PayOrderPresent(oid, this);
        payOrderPresent.requestPayOrderMode();
    }

    @Override
    public void uiShowRefreshLoading() {
        showLoading();
    }

    @Override
    public void uiHideRefreshLoading() {
        hideLoading();
    }

    @Override
    public void renderUIWithData(PayOrderMode item) {
        showActionBar(item.getServiceName());
        ImageHelper.displayCtImage(item.getServiceAva(), ctServiceAva, null);
        ctServiceNameTv.setText(item.getServiceName());
        ctOrderDateBuyerSizeTv.setText(item.getOrderDate() + " - " + item.getBuyerSize());
        ctOrderPriceWhithCurrencyTv.setText(item.getOrderCurrency() + "  " + item.getServiceNormalPrice());

        ctOrderDiscountsTv.setText("您还有"+(item.getDiscountItems() == null ? 0 :item.getDiscountItems().size())+"个优惠可使用");
        if (item.getDiscount() != null) {
            ctOrderDiscountContentTv.setVisibility(View.VISIBLE);
            ctOrderDiscountContentTv.setText("目前使用的优惠码 优惠" + item.getDiscount().getMoneyType() + item.getDiscount().getMoney());
            ctOrderDiscount.setText("-  " + item.getDiscount().getMoney());
            try {
                double value = Double.valueOf(item.getServiceNormalPrice())
                        - Double.valueOf(item.getDiscount().getMoney());
                DecimalFormat df = new DecimalFormat("0.00");
                    ctOrderFinalPriceWithCurrency.setText(item.getOrderCurrency() + "  " + df.format(value));
            } catch (Exception e) {
                ctOrderFinalPriceWithCurrency.setText(item.getOrderCurrency() + "  " + item.getServiceNormalPrice(
                ));
            }
        } else {
            ctOrderDiscountContentTv.setVisibility(View.GONE);
            ctOrderDiscount.setText("-  0");
            ctOrderFinalPriceWithCurrency.setText(item.getOrderCurrency() + "  " + item.getServiceNormalPrice());
        }
        ctOrderNormalPriceWithCurrencyTv.setText(item.getOrderCurrency() + "  " + item.getServiceNormalPrice());
    }

    @Override
    public void disableEvent() {

    }

    @Override
    public void enableEvent() {

    }

    @OnClick(R.id.ct_order_discounts_ll)
    @Override
    public void clickDiscount() {
        payOrderPresent.clickDiscount();
    }

    @OnClick(R.id.pay_tv)
    @Override
    public void clickPayOrder() {
        payOrderPresent.clickPay();
    }

    @Override
    public void uiPayMethod() {
        payMethodPop.showPayMethod();
    }

    @Override
    public void uiShowDiscountCodeInput(PayOrderMode mode) {
        discountSelectPop.showDiscounts(mode);
    }


    public void showMessage(String msg) {
        MessageUtils.showToast(msg);
    }

    @Override
    public void requestPay(String payParams) {
        Intent intent = new Intent();
        String packageName = getPackageName();
        ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
        intent.setComponent(componentName);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, payParams);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    public void uiPaySuc() {
        onPaySuccess();
    }

    @Override
    public void uiPayFailed(String msg) {
    onPayFailed(msg);
    }

    public class PayMethodPop implements View.OnClickListener {

        PopupWindow mPriceTypeWindow;
        @InjectView(R.id.ct_wx_v)
        TextView ctWxV;
        @InjectView(R.id.ct_alipay_v)
        TextView ctAlipayV;
        @InjectView(R.id.empty_view)
        View emptyView;

        public void showPayMethod() {
            if (mPriceTypeWindow == null) {
                View view = LayoutInflater.from(PayOrderAcivity.this).inflate(R.layout.ct_pay_method_pop, null);
                ButterKnife.inject(this, view);
                mPriceTypeWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, MainApplication.getInstance().getPageHeight() - Utils.dp2pixel(48 + 25));
                mPriceTypeWindow.setOutsideTouchable(true);
                ctWxV.setOnClickListener(this);
                ctAlipayV.setOnClickListener(this);
                emptyView.setOnClickListener(this);
            }
            mPriceTypeWindow.showAtLocation(container, Gravity.TOP, 0, Utils.dp2pixel(48 + 25));
            supportInvalidateOptionsMenu();
        }

        public boolean dismiss() {
            if (mPriceTypeWindow != null && mPriceTypeWindow.isShowing()) {
                mPriceTypeWindow.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ct_alipay_v:
                    if (Utils.isAliInstalled()) {
                        payOrderPresent.payWith(PAY_CHANEL_ALIPAY);
                    } else {
                        onPayFailed("请先安装支付宝应用");
                    }
                    break;
                case R.id.ct_wx_v:
                    payOrderPresent.payWith(PAY_CHANEL_WXPAY);
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    public class DiscountAdapter extends BaseAdapter {
        List<DiscountItem> itemList;
        View.OnClickListener onClickListener;

        public DiscountAdapter(List<DiscountItem> itemList, View.OnClickListener onClickListener) {
            this.itemList = itemList;
            this.onClickListener = onClickListener;
        }

        public void setItemList(List<DiscountItem> itemList) {
            this.itemList = itemList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return 1 + (itemList == null ? 0 : itemList.size());
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            }
            return itemList.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_select_item, null);
            DiscountItem item = (DiscountItem) getItem(position);
            if (item != null) {
                ((TextView) view.findViewById(R.id.content)).setText(
                        item.getMoneyType() + item.getMoney()
                );
                LogHelper.e("validate ",item.getInvalidDate());
                LogHelper.e("validate reverted ",Utils.getMsToD(item.getInvalidDate()));
                ((TextView) view.findViewById(R.id.time)).setText(
                                "有效期至" + Utils.getMsToD(item.getInvalidDate())
                );
            } else {
                ((TextView) view.findViewById(R.id.content)).setText(
                        "不使用"
                );
                ((TextView) view.findViewById(R.id.time)).setText(
                        ""
                );
            }
            view.findViewById(R.id.item).setTag(item);
            view.findViewById(R.id.item).setOnClickListener(onClickListener);
            return view;
        }

    }

    public void selectDiscount(DiscountItem discountItem) {
            payOrderPresent.selectDiscount(discountItem);
    }

    public class DiscountSelectPop implements View.OnClickListener {

        PopupWindow mPriceTypeWindow;

        @InjectView(R.id.empty_view)
        View emptyView;
        @InjectView(R.id.progress_bar)
        ProgressBar progressBar;
        @InjectView(R.id.listView)
        ListView listView;
        DiscountAdapter adapter;

        public void showDiscounts(PayOrderMode payOrderMode) {
            if (mPriceTypeWindow == null) {
                View view = LayoutInflater.from(PayOrderAcivity.this).inflate(R.layout.discount_select_list, null);
                ButterKnife.inject(this, view);
                mPriceTypeWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, MainApplication.getInstance().getPageHeight() - Utils.dp2pixel(48 + 25));
                mPriceTypeWindow.setOutsideTouchable(true);
                emptyView.setOnClickListener(this);
            }
            if (payOrderMode.getDiscountItems() == null ) {
                progressBar.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                if (adapter == null || listView.getAdapter() == null) {
                    listView.setAdapter(adapter = new DiscountAdapter(payOrderMode.getDiscountItems(),this));
                } else {
                    adapter.setItemList(payOrderMode.getDiscountItems());
                }

            }
            mPriceTypeWindow.showAtLocation(container, Gravity.TOP, 0, Utils.dp2pixel(48 + 25));
            supportInvalidateOptionsMenu();
        }

        public boolean dismiss() {
            if (mPriceTypeWindow != null && mPriceTypeWindow.isShowing()) {
                mPriceTypeWindow.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item:
                    selectDiscount((DiscountItem) v.getTag());
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    @Override
    protected void onPaySuccess() {
        setResult(RESULT_PAY_SUC);
        String email = "";
        UserInfo userInfo = LoginInstance.getInstance(this).getUserInfo();
        if (userInfo != null) {
            email = userInfo.getEmail();
        }
        PaySucActivity.start(this, email);
        finish();
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
    }

    @Override
    protected void onPayFailed(String msg) {
        setResult(RESULT_PAY_FAILED);

        PayFailedActivity.start(this, msg);
        payOrderPresent.onPayFailedAtClient();
    }


}
