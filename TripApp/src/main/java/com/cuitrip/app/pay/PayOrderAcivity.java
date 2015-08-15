package com.cuitrip.app.pay;

import com.lab.app.BaseActivity;

/**
 * Created by baziii on 15/8/15.
 *
 * 好像用databinding啊啊啊啊啊啊 啊
 */
public class PayOrderAcivity extends BaseActivity implements IPayOrderView {

    PayOrderPresent payOrderPresent;
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
    public void uiShowDiscountCodeInput() {

    }

    @Override
    public void uiShowRemoveDiscountDialog() {

    }
}
