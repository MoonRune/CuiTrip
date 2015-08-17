package com.cuitrip.app.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.OrderFragment;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.pingplusplus.android.PaymentActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
    @InjectView(R.id.ct_order_discount_sw)
    Switch ctOrderDiscountSw;
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
        if (item.isDiscounted()) {
            ctOrderDiscountSw.setChecked(true);
            ctOrderDiscountContentTv.setVisibility(View.VISIBLE);
            ctOrderDiscountContentTv.setText(item.getDiscountDesc());
        } else {
            ctOrderDiscountSw.setChecked(false);
            ctOrderDiscountContentTv.setVisibility(View.GONE);
        }
        ctOrderDiscountSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clickDiscount();
                ctOrderDiscountSw.setChecked(!isChecked);
            }
        });
        ctOrderNormalPriceWithCurrencyTv.setText(item.getOrderCurrency() + "  " + item.getServiceNormalPrice());
        ctOrderDiscount.setText("-  " + item.getDiscountedPrice());
        ctOrderFinalPriceWithCurrency.setText(item.getOrderCurrency() + "  " + item.getServiceNormalPrice());
    }

    @Override
    public void disableEvent() {

    }

    @Override
    public void enableEvent() {

    }

    @Override
    public void clickDiscount() {
        payOrderPresent.clickDiscount();
    }

    @Override
    public void clickPayOrder() {
        payOrderPresent.clickPay();
    }

    @Override
    public void uiPayMethod() {
        payMethodPop.showPayMethod();
    }

    @Override
    public void uiShowDiscountCodeInput() {
        AlertDialog.Builder builder = MessageUtils.createHoloBuilder(this).setTitle(R.string.ct_please_input_freecode);
        final EditText editText = (EditText) LayoutInflater.from(this).inflate(R.layout.single_line_edittext, null);
        builder.setView(
                editText).setPositiveButton(getString(R.string.ct_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String freeCode = editText.getText().toString();
                if (!TextUtils.isEmpty(freeCode)) {
                    payOrderPresent.inputDiscountCode(freeCode);
                } else {
                    MessageUtils.showToast(getString(R.string.ct_freecode_validate_msg));
                }
            }
        }).setCancelable(true).show();
    }

    @Override
    public void uiShowRemoveDiscountDialog() {
        MessageUtils.createHoloBuilder(this).setTitle(R.string.ct_please_confirm_remove_discount).setPositiveButton(getString(R.string.ct_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                payOrderPresent.removeDiscount();
            }
        }).setNegativeButton(R.string.ct_cancel, null).setCancelable(true).show();
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

    @Override
    protected void onPaySuccess() {
        setResult(RESULT_PAY_SUC);
        PaySucActivity.start(this, "  unknown email");
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
