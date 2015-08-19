package com.cuitrip.app.pay;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
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
import com.lab.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by baziii on 15/8/15.
 */
public class PayOrderPresent {

    public static final String DEFAULT_CURRENCY = "cny";
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
                    onResult(count,callback,orderItem,discountItems);
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
                    try {
                        discountItems = JSON.parseArray(data.toString(), DiscountItem.class);

                    } catch (Exception e) {
                    }
                    if (discountItems == null) {
                        discountItems = new ArrayList<>();
                    }
                    count.decrementAndGet();
                    onResult(count,callback,orderItem,discountItems);
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
            }, UnitUtils.getMoenyType());


        }

        public void onResult(AtomicInteger count, final CtFetchCallback<PayOrderMode> callback, OrderItem orderItem,
                             List<DiscountItem> discountItems) {
            if (count.get() <= 0) {
                if (orderItem != null) {
                    callback.onSuc(PayOrderMode.getInstance(orderItem,discountItems));
                }else {

                    callback.onFailed(new CtException( PlatformUtil.getInstance().getString(R.string.ct_get_order_detail_failed)));
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
        iPayOrderView.uiPayMethod();
    }

    public void payWith(String method) {
        iPayOrderView.uiShowRefreshLoading();
        String coupId = "";
        if (payOrderMode != null && payOrderMode.getDiscount() != null) {
            coupId = payOrderMode.getDiscount().getCode();
        }
        payOrderFetcher.getChar(oid, method, DEFAULT_CURRENCY, Utils.getLocalHostIp(), coupId, new CtFetchCallback<String>() {
            @Override
            public void onSuc(String s) {
                iPayOrderView.uiHideRefreshLoading();
                iPayOrderView.requestPay(s);
            }

            @Override
            public void onFailed(CtException throwable) {
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
