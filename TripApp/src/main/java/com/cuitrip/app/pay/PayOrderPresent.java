package com.cuitrip.app.pay;

import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/15.
 */
public class PayOrderPresent {

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

        @Override
        public void fetchPayOrder(String oid, CtFetchCallback<PayOrderMode> callback) {

        }

        @Override
        public void setDiscount(String oid, String discountCode, CtApiCallback callback) {

        }

        @Override
        public void removeDiscount(String oid, CtApiCallback callback) {

        }
    };

    public void requestPayOrderMode() {
        iPayOrderView.uiShowRefreshLoading();
        payOrderFetcher.fetchPayOrder(oid, new CtFetchCallback<PayOrderMode>() {
            @Override
            public void onSuc(PayOrderMode payOrderMode) {
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
        if (payOrderMode.isDiscounted()) {
            iPayOrderView.uiShowRemoveDiscountDialog();
        } else {
            iPayOrderView.uiShowDiscountCodeInput();
        }
    }

    public void clickPay() {

    }

    public void inputDiscountCode(String code) {

    }


    public void removeDiscount() {

    }
}
