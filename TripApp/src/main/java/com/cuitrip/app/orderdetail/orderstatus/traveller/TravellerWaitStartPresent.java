package com.cuitrip.app.orderdetail.orderstatus.traveller;

import com.cuitrip.app.orderdetail.ITravelerOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

/**
 * Created by baziii on 15/8/11.
 */
public class TravellerWaitStartPresent extends BaseOrderFormPresent<ITravelerOrderDetailView> {

    public TravellerWaitStartPresent(ITravelerOrderDetailView orderDetailView, OrderItem orderItem) {
        super(orderDetailView, orderItem);
    }

    @Override
    public void render() {
        mOrderDetailView.renderUi(build(mOrderItem));
    }

    @Override
    public void clickBottom() {
        mOrderDetailView.jumpMapOrder(mOrderItem);
    }

    @Override
    public void clickMenu() {
        mOrderDetailView.jumpCancelOrder(mOrderItem);
    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return "";
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
