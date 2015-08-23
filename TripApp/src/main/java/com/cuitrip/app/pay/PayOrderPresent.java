package com.cuitrip.app.pay;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.model.DiscountItem;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by baziii on 15/8/15.
 */
public class PayOrderPresent {

    public static final String TAG = "PayOrderPresent";
    String oid;
    IPayOrderView iPayOrderView;

    public PayOrderPresent(String oid, IPayOrderView iPayOrderView) {
        this.oid = oid;
        this.iPayOrderView = iPayOrderView;
    }

    PayOrderMode payOrderMode;

    IPayOrderFetcher payOrderFetcher = new IPayOrderFetcher() {
        // order item exists only at api connect; so while mock data ,IPayOrderFetcher does not contains orderitem;
        OrderItem orderItem;
        List<DiscountItem> discountItems;
        AsyncHttpClient mClient = new AsyncHttpClient();

        @Override
        public void fetchPayOrder(String oid, final CtFetchCallback<PayOrderMode> callback) {
            final AtomicInteger count = new AtomicInteger(2);
            OrderBusiness.getOrderInfo(((PayOrderAcivity) iPayOrderView), mClient, new LabAsyncHttpResponseHandler(OrderItem.class) {
                @Override
                public void onSuccess(LabResponse response, Object data) {

                    orderItem = ((OrderItem) data);
                    count.decrementAndGet();
                    onResult(count, callback, orderItem, discountItems);
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    String reason;
                    if (response != null && !TextUtils.isEmpty(response.msg)) {
                        reason = response.msg;
                    } else {
                        reason = PlatformUtil.getInstance().getString(R.string.ct_get_order_detail_failed);
                    }
                    callback.onFailed(new CtException(reason));

                }
            }, String.valueOf(oid));

            OrderBusiness.getValidCoupon(((PayOrderAcivity) iPayOrderView), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    LogHelper.e(TAG, " result " + String.valueOf(response.result));
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(response.result));

                        LogHelper.e(TAG, " result  list s " + jsonObject.getString("lists"));
                        discountItems = JSON.parseArray(jsonObject.getString("lists"), DiscountItem.class);

                    } catch (Exception e) {
                        LogHelper.e(TAG, " result  error" + e.getMessage());
                    }
                    if (discountItems == null) {
                        discountItems = new ArrayList<>();
                    }
                    count.decrementAndGet();
                    onResult(count, callback, orderItem, discountItems);
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    String reason;
                    if (response != null && !TextUtils.isEmpty(response.msg)) {
                        reason = response.msg;
                    } else {
                        reason = PlatformUtil.getInstance().getString(R.string.ct_get_order_detail_failed);
                    }
                    callback.onFailed(new CtException(reason));

                }
            }, UnitUtils.getMoneyType());


        }

        public void onResult(AtomicInteger count, final CtFetchCallback<PayOrderMode> callback, OrderItem orderItem,
                             List<DiscountItem> discountItems) {
            if (count.get() <= 0) {
                if (orderItem != null) {
                    callback.onSuc(PayOrderMode.getInstance(orderItem, discountItems));
                } else {
                    callback.onFailed(new CtException(PlatformUtil.getInstance().getString(R.string.ct_get_order_detail_failed)));
                }
            }
        }

        @Override
        public void setDiscount(String oid, String discountCode, final CtApiCallback callback) {
            OrderBusiness.payOrder(((PayOrderAcivity) iPayOrderView), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    callback.onSuc();
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    String msg = response.msg;
                    if (TextUtils.isEmpty(msg)) {
                        msg = PlatformUtil.getInstance().getString(R.string.ct_pay_failed);
                    }
                    callback.onFailed(new CtException(msg));
                }

            }, oid, discountCode);
        }

        @Override
        public void removeDiscount(String oid, CtApiCallback callback) {

        }

        @Override
        public void getChar(String oid, String type, String ip, String currency, String couponId, final CtFetchCallback<String> callback) {
            LogHelper.e(TAG, TextUtils.join("|", new String[]{oid, type, ip, currency, couponId}));
            OrderBusiness.getCharge(((PayOrderAcivity) iPayOrderView), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    callback.onSuc(String.valueOf(data));
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    String msg = response.msg;
                    if (TextUtils.isEmpty(msg)) {
                        msg = PlatformUtil.getInstance().getString(R.string.ct_pay_failed);
                    }
                    callback.onFailed(new CtException(msg));
                }

            }, oid, type, ip, currency, couponId);
        }

        @Override
        public void notifiOrderStatus(String oid, CtApiCallback callback) {

        }

        @Override
        public void needCharge(String oid, String currency, String couponId, final CtFetchCallback<Boolean> callback) {
            LogHelper.e(TAG, TextUtils.join("|", new String[]{oid, currency, couponId}));
            OrderBusiness.getFinalPrice(((PayOrderAcivity) iPayOrderView), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    LogHelper.e(TAG, " need char " + String.valueOf(response.result));
                    JSONObject jsonObject = JSONObject.parseObject(String.valueOf(data));
                    double value = 0;

                    try {
                        value = Double.valueOf(jsonObject.getString("finalPrice"));
                    } catch (NumberFormatException e) {
                        value = 1;
                    }
                    callback.onSuc(value > 0);
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    String msg = response.msg;
                    if (TextUtils.isEmpty(msg)) {
                        msg = PlatformUtil.getInstance().getString(R.string.ct_pay_failed);
                    }
                    callback.onFailed(new CtException(msg));
                }

            }, oid, currency, couponId);
        }
    };

    public void requestPayOrderMode() {
        iPayOrderView.uiShowRefreshLoading();
        payOrderFetcher.fetchPayOrder(oid, new CtFetchCallback<PayOrderMode>() {
            @Override
            public void onSuc(PayOrderMode payOrderMode) {
                PayOrderPresent.this.payOrderMode = payOrderMode;
                iPayOrderView.renderUIWithData(payOrderMode);
                iPayOrderView.uiHideRefreshLoading();
            }

            @Override
            public void onFailed(CtException throwable) {
                iPayOrderView.uiHideRefreshLoading();

            }
        });
    }

    public void clickDiscount() {
        iPayOrderView.uiShowDiscountCodeInput(payOrderMode);
    }

    public void selectDiscount(DiscountItem discountItem) {
        payOrderMode.setDiscount(discountItem);
        iPayOrderView.renderUIWithData(payOrderMode);
    }


    public void clickPay() {

        iPayOrderView.uiShowRefreshLoading();
        String temp = "";
        if (payOrderMode != null && payOrderMode.getDiscount() != null) {
            temp = payOrderMode.getDiscount().getCode();
        }
        final String coupId = temp;
        payOrderFetcher.needCharge(oid, UnitUtils.getMoneyType(), coupId, new CtFetchCallback<Boolean>() {
            @Override
            public void onSuc(Boolean aBoolean) {
                if (aBoolean) {
                    iPayOrderView.uiHideRefreshLoading();
                    iPayOrderView.uiPayMethod();
                } else {
                    payOrderFetcher.getChar(oid, "", Utils.getLocalHostIp(), UnitUtils.getMoneyType(), coupId, new CtFetchCallback<String>() {
                        @Override
                        public void onSuc(String s) {
                            iPayOrderView.uiHideRefreshLoading();
                            iPayOrderView.uiPaySuc();
                        }

                        @Override
                        public void onFailed(CtException throwable) {
                            iPayOrderView.uiHideRefreshLoading();
                            iPayOrderView.uiPayFailed(throwable.getMessage());

                        }
                    });
                }

            }

            @Override
            public void onFailed(CtException throwable) {
                MessageUtils.showToast(throwable.getMessage());
                iPayOrderView.uiHideRefreshLoading();

            }
        });
    }

    public void payWith(String method) {
        iPayOrderView.uiShowRefreshLoading();
        String coupId = "";
        if (payOrderMode != null && payOrderMode.getDiscount() != null) {
            coupId = payOrderMode.getDiscount().getCode();
        }
        payOrderFetcher.getChar(oid, method, Utils.getLocalHostIp(), UnitUtils.getMoneyType(), coupId, new CtFetchCallback<String>() {
            @Override
            public void onSuc(String s) {
                iPayOrderView.uiHideRefreshLoading();
                iPayOrderView.requestPay(s);
            }

            @Override
            public void onFailed(CtException throwable) {
                MessageUtils.showToast(throwable.getMessage());
                iPayOrderView.uiHideRefreshLoading();

            }
        });
    }

    public void inputDiscountCode(String code) {
        iPayOrderView.uiShowRefreshLoading();

        payOrderFetcher.setDiscount(oid, code, new CtApiCallback() {
            @Override
            public void onSuc() {
                iPayOrderView.showMessage(PlatformUtil.getInstance().getString(R.string.ct_discount_suc_message));
                requestPayOrderMode();
            }

            @Override
            public void onFailed(CtException throwable) {
                iPayOrderView.showMessage(throwable.getMessage());
                iPayOrderView.uiHideRefreshLoading();

            }
        });
    }


    public void removeDiscount() {
        iPayOrderView.uiShowRefreshLoading();
        payOrderFetcher.removeDiscount(oid, new CtApiCallback() {
            @Override
            public void onSuc() {
                iPayOrderView.showMessage(PlatformUtil.getInstance().getString(R.string.ct_cancel_discount_suc_msg));
                requestPayOrderMode();
            }

            @Override
            public void onFailed(CtException throwable) {
                iPayOrderView.showMessage(throwable.getMessage());
                iPayOrderView.uiHideRefreshLoading();

            }
        });
    }

    public void onPaySucAtClient() {
        payOrderFetcher.notifiOrderStatus(oid, new CtApiCallback() {
            @Override
            public void onSuc() {

            }

            @Override
            public void onFailed(CtException throwable) {

            }
        });
    }

    public void onPayFailedAtClient() {
        payOrderFetcher.notifiOrderStatus(oid, new CtApiCallback() {
            @Override
            public void onSuc() {

            }

            @Override
            public void onFailed(CtException throwable) {

            }
        });
    }
}
