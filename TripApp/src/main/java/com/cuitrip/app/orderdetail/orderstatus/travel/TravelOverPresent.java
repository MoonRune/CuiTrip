package com.cuitrip.app.orderdetail.orderstatus.travel;

import com.cuitrip.app.orderdetail.IFinderOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.app.pro.CommentPartRenderData;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/11.
 */
public class TravelOverPresent extends BaseOrderFormPresent<IFinderOrderDetailView> {

    public TravelOverPresent(IFinderOrderDetailView orderDetailView, OrderItem orderItem) {
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
    public CommentPartRenderData buildCommenetData(OrderItem orderItem) {
        CommentPartRenderData renderData =new CommentPartRenderData(
                orderItem.getInsiderName()+"的评论",
                orderItem.getHeadPic(),
                orderItem.getCommentScore() ,
                CommentPartRenderData.DEFAULT_MAX_SCORE,
                orderItem.getComment()
        );
        return renderData;
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
