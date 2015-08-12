package com.cuitrip.app.orderdetail.orderstatus.finder;

import com.cuitrip.app.orderdetail.ITravelerOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/11.
 */
public class FinderWaitPayPresent extends BaseOrderFormPresent<ITravelerOrderDetailView> {

    public FinderWaitPayPresent(ITravelerOrderDetailView orderDetailView, OrderItem orderItem) {
        super(orderDetailView, orderItem);
    }

    @Override
    public void render() {
        mOrderDetailView.renderUi(build(mOrderItem));
    }

    @Override
    public void clickBottom() {
        mOrderDetailView.jumpPayOrder(mOrderItem);
    }

    @Override
    public void clickMenu() {
        mOrderDetailView.jumpCancelOrder(mOrderItem);

    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return "立即支付";
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return "取消预订";
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return true;
    }
}
