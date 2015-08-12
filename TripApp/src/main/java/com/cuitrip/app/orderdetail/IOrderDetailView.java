package com.cuitrip.app.orderdetail;

import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/11.
 */
public interface IOrderDetailView {
    void showLoading();
    void hideLoading();
    void renderUi(OrderMode orderMode);
    void requestPresentRender(OrderItem orderItem);
}
