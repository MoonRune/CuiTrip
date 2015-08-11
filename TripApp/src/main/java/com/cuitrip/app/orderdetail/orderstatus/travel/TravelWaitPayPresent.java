package com.cuitrip.app.orderdetail.orderstatus.travel;

import com.cuitrip.app.orderdetail.OrderMode;

/**
 * Created by baziii on 15/8/11.
 */
public interface TravelWaitPayPresent {
    void payOrder(OrderMode mode);
    void cancelOrder(OrderMode mode);
}
