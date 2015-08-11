package com.cuitrip.app.orderdetail.orderstatus.travel;

import com.cuitrip.app.orderdetail.OrderMode;

/**
 * Created by baziii on 15/8/11.
 */
public interface TravelWaitConfirmPresent {
    void modifyOrder(OrderMode mode);
    void cancelOrder(OrderMode mode);
}
