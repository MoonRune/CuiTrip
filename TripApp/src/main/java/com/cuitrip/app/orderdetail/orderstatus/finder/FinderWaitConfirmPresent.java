package com.cuitrip.app.orderdetail.orderstatus.finder;

import com.cuitrip.app.orderdetail.OrderMode;

/**
 * Created by baziii on 15/8/11.
 */
public interface FinderWaitConfirmPresent {
    void confirmOrder(OrderMode mode);
    void refuseOrder(OrderMode mode);
}
