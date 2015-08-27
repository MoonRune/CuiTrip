package com.cuitrip.app.orderdetail.orderstatus;

import com.cuitrip.app.orderdetail.IOrderDetailView;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/12.
 */
public abstract class IOrderFormPresent<T extends IOrderDetailView> {
    protected T mOrderDetailView;
    protected OrderItem mOrderItem;

    public IOrderFormPresent(T orderDetailView) {
        mOrderDetailView = orderDetailView;
    }

    public void setOrderItem(OrderItem orderItem) {
        mOrderItem = orderItem;
    }

    public abstract void render();

    public abstract void clickBottom();

    public abstract void clickMenu();

}
