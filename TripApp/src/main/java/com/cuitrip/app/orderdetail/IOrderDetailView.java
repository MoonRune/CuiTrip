package com.cuitrip.app.orderdetail;

/**
 * Created by baziii on 15/8/11.
 */
public interface IOrderDetailView {
    void switchWaitConfirm();
    void switchWaitPay();
    void switchWaitStart();
    void switchWaitEnd();
    void switchWaitComment();
    void switchOver();
    void switchUnvaliable();
}
