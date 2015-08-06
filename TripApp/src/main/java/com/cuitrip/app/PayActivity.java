package com.cuitrip.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cuitrip.business.OrderBusiness;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.CurrencyHelper;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/7/30.
 */
public class PayActivity extends BaseActivity implements OnClickListener {
    public static final String DEFAULT_CURRENCY = "cny";
    public static final String TAG = "PayActivity";
    public static final String ORDER_ID_KEY = "ORDER_ID_KEY";
    public static final int REQUEST_FOR_PAY = 1000;
    public static final int RESULT_PAY_SUC = 1001;
    public static final int RESULT_PAY_FAILED = 1010;
    @InjectView(R.id.ct_order_banner_iv)
    ImageView mOrderBannerIv;
    @InjectView(R.id.ct_order_name_tv)
    TextView mOrderNameTv;
    @InjectView(R.id.ct_order_date_tv)
    TextView mOrderDateTv;
    @InjectView(R.id.ct_order_people_size_tv)
    TextView mOrderPeopleSizeTv;
    @InjectView(R.id.ct_order_remote_cost_tv)
    TextView mOrderRemoteCostTv;
    @InjectView(R.id.ct_order_local_cost_tv)
    TextView mOrderLocalCostTv;
    @InjectView(R.id.ct_order_info_ll)
    LinearLayout mOrderInfoLl;
    @InjectView(R.id.ct_free_code_v)
    TextView mFreeCodeV;
    @InjectView(R.id.ct_alipay_v)
    View mAlipayV;
    @InjectView(R.id.ct_wxpay_v)
    View mWxpayV;
    @InjectView(R.id.ct_order_local_currency_tv)
    TextView mOrderLocalCurrencyTv;

    AsyncHttpClient mClient = new AsyncHttpClient();
    String mOrderId;
    OrderItem mOrderInfo;
    @InjectView(R.id.ct_abstract_line_tv)
    TextView mAbstractLineTv;

    public static void startActivity(Activity context, String orderid) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(ORDER_ID_KEY, orderid);
        context.startActivityForResult(intent, REQUEST_FOR_PAY);
    }

    public static boolean isPaySUc(int requestCode, int resultCode, Intent data) {
        return requestCode == REQUEST_FOR_PAY && resultCode == RESULT_PAY_SUC;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_pay);
        ButterKnife.inject(this);
        mOrderId = getIntent().getStringExtra(ORDER_ID_KEY);
        mAbstractLineTv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        resetFreeButtonPrice();
        fetchOrderInfo();

    }

    protected void fetchOrderInfo() {
        showLoading();
        OrderBusiness.getOrderInfo(this, mClient, new LabAsyncHttpResponseHandler(OrderItem.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideLoading();
                if (data != null) {
                    mOrderInfo = (OrderItem) data;
                    if (mOrderInfo.getStatus() == 2) {//进行中
                        LogHelper.e(TAG, " ordre =" + mOrderInfo.getOid() + "|" + mOrderInfo.getPayCurrency());
                        render();
                    } else {
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                }
                return;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
            }
        }, String.valueOf(mOrderId));
    }

    @OnClick(R.id.service_back)
    public void clickBack() {
        finish();
    }

    public void render() {
        ImageHelper.displayCtImage(mOrderInfo.getServicePIC(), mOrderBannerIv, null);
        mOrderNameTv.setText(mOrderInfo.getServiceName());
        String time = mOrderInfo.getServiceDate();
        if (time != null && time.indexOf(" ") > 1) {
            time = time.substring(0, time.indexOf(" "));
        } else {
            time = mOrderInfo.getServiceDate();
        }

        mOrderDateTv.setText(Html.fromHtml(getString(R.string.ct_order_info_date, time)));
        mOrderPeopleSizeTv.setText(Html.fromHtml(getString(R.string.ct_order_info_people, mOrderInfo.getBuyerNum())));
        String append = mOrderInfo.isPricePerMan() ? getString(R.string.ct_per_man) : "";
        mOrderRemoteCostTv.setText(Html.fromHtml(getString(R.string.ct_order_info_service_price, mOrderInfo.getServicePrice(),
                CurrencyHelper.getInstance().getCurrencyName(mOrderInfo.getMoneyType())) + append));
        mOrderLocalCurrencyTv.setText(Html.fromHtml(getString(R.string.ct_order_info_pay_currency,
                CurrencyHelper.getInstance().getCurrencyName(mOrderInfo.getPayCurrency()))));
        mOrderLocalCostTv.setText(String.valueOf(mOrderInfo.getOrderPrice()));

        if (!mOrderInfo.isDiscount()) {
            mFreeCodeV.setVisibility(View.VISIBLE);
            mFreeCodeV.setOnClickListener(this);
            resetFreeButtonPrice();
        }else {
            mFreeCodeV.setVisibility(View.GONE);
        }
        mAlipayV.setOnClickListener(this);
        mWxpayV.setOnClickListener(this);
    }

    public void resetFreeButtonPrice() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFreeCodeV.getLayoutParams();
        int height = mFreeCodeV.getHeight();
        if (height <= 0) {
            height = mFreeCodeV.getMeasuredHeight();
        }
        params.setMargins(0, -height / 2, 0, 0);
        mFreeCodeV.setLayoutParams(params);

        LinearLayout.LayoutParams localParams = (LinearLayout.LayoutParams) mOrderLocalCostTv.getLayoutParams();
        localParams.setMargins(0, 0, 0, height * 2 / 3);
        mOrderLocalCostTv.setLayoutParams(localParams);
    }

    protected void requestForOrderInfo(String type) {
        showLoading();
        OrderBusiness.getCharge(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                LogHelper.e(TAG, "onsuc");
                pay(String.valueOf(data));
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                LogHelper.e(TAG, "onfailed " + response.msg);
                String msg = response.msg;
                if (TextUtils.isEmpty(msg)) {
                    msg = getString(R.string.ct_pay_failed);
                }
                onPayFailed(msg);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
            }
        }, mOrderInfo.getOid(), type, getLocalHostIp(), DEFAULT_CURRENCY);
    }

    public String getLocalHostIp() {
        String ipaddress = "127.0.0.1";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ipaddress = ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            return ipaddress;
        }
        return ipaddress;

    }

    public void showFreeCodeInputDialog() {
        AlertDialog.Builder builder = MessageUtils.createHoloBuilder(this).setTitle(R.string.ct_please_input_freecode);
        final EditText editText = (EditText) LayoutInflater.from(this).inflate(R.layout.single_line_edittext, null);
        builder.setView(
                editText).setPositiveButton(getString(R.string.ct_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String freeCode = editText.getText().toString();
                if (!TextUtils.isEmpty(freeCode)) {
                    payWithFreeCode(freeCode);
                } else {
                    MessageUtils.showToast(getString(R.string.ct_freecode_validate_msg));
                }
            }
        }).setCancelable(true).show();
    }

    public void payWithFreeCode(String code) {
        showLoading();
        OrderBusiness.payOrder(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                freeCodeContinueRefresh();
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                String msg = response.msg;
                if (TextUtils.isEmpty(msg)) {
                    msg = getString(R.string.ct_pay_failed);
                }
                onPayFailed(msg);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
            }
        }, mOrderId, code);

    }

    public void freeCodeContinueRefresh() {
        showLoading();
        OrderBusiness.getOrderInfo(this, mClient, new LabAsyncHttpResponseHandler(OrderItem.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideLoading();
                if (data != null) {
                    mOrderInfo = (OrderItem) data;
                    if (mOrderInfo.getStatus() == 8) {
                        onPaySuccess();
                    } else {
                        MessageUtils.showToast(getString(R.string.ct_discount_suc));
                        render();
                    }
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                }
                return;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
            }
        }, String.valueOf(mOrderId));
    }

    @Override
    protected void onPaySuccess() {
        setResult(RESULT_PAY_SUC);
        MessageUtils.dialogBuilder(this, false, null, getString(R.string.ct_pay_suc), getString(R.string.ct_pay_suc_content), getString(R.string.ct_watch_trips), new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        OrderBusiness.notifyPayStatus(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {

            }

            @Override
            public void onFailure(LabResponse response, Object data) {

            }
        }, mOrderId, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
    }

    @Override
    protected void onPayFailed(String msg) {
        setResult(RESULT_PAY_FAILED);
        MessageUtils.dialogBuilder(this, false, null, getString(R.string.ct_pay_failed), msg, getString(R.string.ct_i_know), null);
        OrderBusiness.notifyPayStatus(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
            }
        }, mOrderId, false);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ct_alipay_v:
                if (Utils.isAliInstalled()) {
                    requestForOrderInfo(PAY_CHANEL_ALIPAY);
                } else {
                    onPayFailed("请先安装支付宝应用");
                }
                break;

            case R.id.ct_wxpay_v:
                requestForOrderInfo(PAY_CHANEL_WXPAY);
                break;

            case R.id.ct_free_code_v:
                showFreeCodeInputDialog();
                break;

            default:
                break;
        }
    }
}
