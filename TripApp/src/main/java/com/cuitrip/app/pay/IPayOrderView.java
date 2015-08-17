package com.cuitrip.app.pay;

import com.cuitrip.app.base.IRefreshView;

/**
 * Created by baziii on 15/8/15.
 */
public interface IPayOrderView extends IRefreshView<PayOrderMode> {
    void clickDiscount();
    void clickPayOrder();

    void uiPayMethod();
    void uiShowDiscountCodeInput();
    void uiShowRemoveDiscountDialog();
    void showMessage(String msg);
    void requestPay(String payParams);
}
