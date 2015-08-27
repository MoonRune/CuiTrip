package com.cuitrip.app.orderdetail;

import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/12.
 */
public interface ITravelerOrderDetailView extends IOrderDetailView{
    void jumpModifyOrder(OrderItem orderItem);
    void jumpCancelOrder(OrderItem orderItem);
    void jumpPayOrder(OrderItem orderItem);
    void jumpMapOrder(OrderItem orderItem);
    void jumpCommentOrder(OrderItem orderItem);
    void jumpHelp(OrderItem orderItem);
}
