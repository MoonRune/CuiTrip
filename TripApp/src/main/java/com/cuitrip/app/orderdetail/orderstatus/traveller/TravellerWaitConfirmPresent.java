package com.cuitrip.app.orderdetail.orderstatus.traveller;

import com.cuitrip.app.orderdetail.ITravelerOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

/**
 * Created by baziii on 15/8/11.
 */
public class TravellerWaitConfirmPresent extends BaseOrderFormPresent<ITravelerOrderDetailView> {

    public TravellerWaitConfirmPresent(ITravelerOrderDetailView orderDetailView, OrderItem orderItem) {
        super(orderDetailView, orderItem);
    }

    @Override
    public void render() {
        mOrderDetailView.renderUi(build(mOrderItem));
    }

    @Override
    public void clickBottom() {
        mOrderDetailView.jumpModifyOrder(mOrderItem);
    }

    @Override
    public void clickMenu() {
        mOrderDetailView.jumpCancelOrder(mOrderItem);
    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return PlatformUtil.getInstance().getString(R.string.modify_order_text);
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return PlatformUtil.getInstance().getString(R.string.cancel_order_text);
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return true;
    }


}
