package com.cuitrip.app.orderdetail.orderstatus.finder;

import com.cuitrip.app.orderdetail.IFinderOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.app.pro.OrderProgressingRenderData;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

/**
 * Created by baziii on 15/8/11.
 */
public class FinderWaitEndPresent extends BaseOrderFormPresent<IFinderOrderDetailView> {

    public FinderWaitEndPresent(IFinderOrderDetailView orderDetailView, OrderItem orderItem) {
        super(orderDetailView, orderItem);
    }

    @Override
    public void render() {
        mOrderDetailView.renderUi(build(mOrderItem));
    }

    @Override
    public void clickBottom() {
        mOrderDetailView.jumpEndOrder(mOrderItem);
    }

    @Override
    public void clickMenu() {
        mOrderDetailView.jumpHelp(mOrderItem);
    }

    public OrderProgressingRenderData buildOrderProgress(OrderItem orderItem) {
        return OrderProgressingRenderData.getInstance(orderItem);
    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return PlatformUtil.getInstance().getString(R.string.end_order_text);
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return PlatformUtil.getInstance().getString(R.string.connect_cuitrip);
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return true;
    }

}
