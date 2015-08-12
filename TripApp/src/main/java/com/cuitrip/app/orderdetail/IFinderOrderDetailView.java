package com.cuitrip.app.orderdetail;

import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/12.
 */
public interface IFinderOrderDetailView extends IOrderDetailView {
    void jumpConfirmOrder(OrderItem orderItem);
    void jumpRefuseOrder(OrderItem orderItem);
    void jumpStartOrder(OrderItem orderItem);
}
