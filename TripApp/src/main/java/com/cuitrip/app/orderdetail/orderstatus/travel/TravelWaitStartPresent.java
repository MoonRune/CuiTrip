package com.cuitrip.app.orderdetail.orderstatus.travel;

import com.cuitrip.app.orderdetail.IFinderOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/11.
 */
public class TravelWaitStartPresent extends BaseOrderFormPresent<IFinderOrderDetailView> {

    public TravelWaitStartPresent(IFinderOrderDetailView orderDetailView, OrderItem orderItem) {
        super(orderDetailView, orderItem);
    }

    @Override
    public void render() {
        mOrderDetailView.renderUi(build(mOrderItem));
    }

    @Override
    public void clickBottom() {
        mOrderDetailView.jumpStartOrder(mOrderItem);
    }

    @Override
    public void clickMenu() {
    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return "开始旅程";
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return "";
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return true;
    }

}
