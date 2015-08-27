package com.cuitrip.app.orderdetail.orderstatus.finder;

import com.cuitrip.app.orderdetail.IFinderOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

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
        return PlatformUtil.getInstance().getString(R.string.confirm_order_text);
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return PlatformUtil.getInstance().getString(R.string.refuse_oder_text);
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return true;
    }

}