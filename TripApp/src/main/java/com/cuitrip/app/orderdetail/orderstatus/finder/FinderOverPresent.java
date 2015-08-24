package com.cuitrip.app.orderdetail.orderstatus.finder;

import com.cuitrip.app.orderdetail.IFinderOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.app.pro.CommentPartRenderData;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

/**
 * Created by baziii on 15/8/11.
 */
public class FinderOverPresent extends BaseOrderFormPresent<IFinderOrderDetailView> {

    public FinderOverPresent(IFinderOrderDetailView orderDetailView, OrderItem orderItem) {
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
                 PlatformUtil.getInstance().getString(R.string.someones_comment_with_name,orderItem.getUserNick()),
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
