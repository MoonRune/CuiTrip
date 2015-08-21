package com.cuitrip.app.orderdetail.orderstatus.finder;

import com.cuitrip.app.orderdetail.IFinderOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/11.
 */
public class FinderWaitConfirmPresent extends BaseOrderFormPresent<IFinderOrderDetailView> {

    public FinderWaitConfirmPresent(IFinderOrderDetailView orderDetailView, OrderItem orderItem) {
        super(orderDetailView, orderItem);
    }

    @Override
    public void render() {
        mOrderDetailView.renderUi(build(mOrderItem));
    }

    @Override
    public void clickBottom() {
        mOrderDetailView.jumpConfirmOrder(mOrderItem);
    }

    @Override
    public void clickMenu() {
        mOrderDetailView.jumpRefuseOrder(mOrderItem);
    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return "确认预订";
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return "拒绝";
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return true;
    }

}