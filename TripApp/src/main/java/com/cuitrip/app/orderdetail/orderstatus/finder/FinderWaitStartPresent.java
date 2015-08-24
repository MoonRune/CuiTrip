package com.cuitrip.app.orderdetail.orderstatus.finder;

import com.cuitrip.app.orderdetail.IFinderOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

/**
 * Created by baziii on 15/8/11.
 */
public class FinderWaitStartPresent extends BaseOrderFormPresent<IFinderOrderDetailView> {

    public FinderWaitStartPresent(IFinderOrderDetailView orderDetailView, OrderItem orderItem) {
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
        mOrderDetailView.jumpCancelOrder(mOrderItem);
    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return PlatformUtil.getInstance().getString(R.string.begin_order_text);
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
