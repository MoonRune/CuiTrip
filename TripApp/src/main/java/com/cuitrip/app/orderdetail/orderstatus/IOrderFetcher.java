package com.cuitrip.app.orderdetail.orderstatus;

import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/12.
 */
public interface IOrderFetcher {
    void refuseOrder(OrderItem orderItem, String reason, CtApiCallback callback);

    void confirmOrder(OrderItem orderItem, CtApiCallback callback);

    void startOrder(OrderItem orderItem, CtApiCallback callback);

    void endOrder(OrderItem orderItem, CtApiCallback callback);
}
