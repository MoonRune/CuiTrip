package com.cuitrip.app.orderdetail.orderstatus;

import com.cuitrip.app.orderdetail.IOrderDetailView;
import com.cuitrip.app.orderdetail.OrderMode;
import com.cuitrip.app.pro.CommentPartRenderData;
import com.cuitrip.app.pro.OrderProgressingRenderData;
import com.cuitrip.app.pro.ServicePartRenderData;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/12.
 */
public abstract class BaseOrderFormPresent<T extends IOrderDetailView> extends IOrderFormPresent<T> {


    public BaseOrderFormPresent(T orderDetailView, OrderItem orderItem) {
        super(orderDetailView);
        setOrderItem(orderItem);
    }


    public OrderMode build(OrderItem orderItem) {
        ServicePartRenderData servicePartRenderData = builServiceData(orderItem);
        if (servicePartRenderData!=null) {
            servicePartRenderData.setUnvaliableReason(getOrderUnvaliableReason(orderItem));
        }
        OrderMode result = new OrderMode(servicePartRenderData,
                buildCommenetData(orderItem),
                buildOrderProgress(orderItem), getMenuText(orderItem), getBottomText(orderItem), getBottomEnable(orderItem));
        return result;
    }

    public String getOrderUnvaliableReason(OrderItem orderItem){
        return null;
    }
    public CommentPartRenderData buildCommenetData(OrderItem orderItem) {
        return null;
    }

    public OrderProgressingRenderData buildOrderProgress(OrderItem orderItem) {
        return null;
    }



    public abstract String getBottomText(OrderItem orderItem);

    public abstract String getMenuText(OrderItem orderItem);

    public abstract boolean getBottomEnable(OrderItem orderItem);

    public ServicePartRenderData builServiceData(OrderItem orderItem) {
        return ServicePartRenderData.getInstance(orderItem);
    }

}
