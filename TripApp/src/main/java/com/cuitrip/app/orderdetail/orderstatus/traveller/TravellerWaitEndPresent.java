package com.cuitrip.app.orderdetail.orderstatus.traveller;

import com.cuitrip.app.orderdetail.ITravelerOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.app.pro.OrderProgressingRenderData;
import com.cuitrip.app.pro.ServicePartRenderData;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/11.
 */
public class TravellerWaitEndPresent extends BaseOrderFormPresent<ITravelerOrderDetailView> {

    public TravellerWaitEndPresent(ITravelerOrderDetailView orderDetailView, OrderItem orderItem) {
        super(orderDetailView, orderItem);
    }

    @Override
    public void render() {
        mOrderDetailView.renderUi(build(mOrderItem));
    }

    @Override
    public void clickBottom() {
    }

    @Override
    public void clickMenu() {
        mOrderDetailView.jumpHelp(mOrderItem);
    }

    public OrderProgressingRenderData buildOrderProgress(OrderItem orderItem) {
        return OrderProgressingRenderData.getInstance(orderItem);
    }

    public ServicePartRenderData builServiceData(OrderItem orderItem) {
       return null;
    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return "";
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return "寻求帮助";
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return true;
    }


}
