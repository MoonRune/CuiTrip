package com.cuitrip.app.orderdetail.orderstatus.finder;

import com.cuitrip.app.orderdetail.OrderMode;

/**
 * Created by baziii on 15/8/11.
 */
public interface FinderWaitEndPresent {
    void callHelp(OrderMode mode);
    void endOrder(OrderMode mode);
}
