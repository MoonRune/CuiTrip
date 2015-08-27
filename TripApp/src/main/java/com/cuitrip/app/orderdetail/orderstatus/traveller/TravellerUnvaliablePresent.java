package com.cuitrip.app.orderdetail.orderstatus.traveller;

import com.cuitrip.app.orderdetail.ITravelerOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.app.pro.ServicePartRenderData;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/11.
 */
public class TravellerUnvaliablePresent extends BaseOrderFormPresent<ITravelerOrderDetailView> {

    public TravellerUnvaliablePresent(ITravelerOrderDetailView orderDetailView, OrderItem orderItem) {
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

    }

    @Override
    public String getOrderUnvaliableReason(OrderItem orderItem){
        return ServicePartRenderData.getUnvaliableReason(orderItem);
    }
    @Override
    public String getBottomText(OrderItem orderItem) {
        return "";
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return "";
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return false;
    }

}
