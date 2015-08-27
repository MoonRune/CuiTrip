package com.cuitrip.app.base;

/**
 * Created by baziii on 15/8/10.
 */
public interface IRefreshView<T> extends IView {
    void uiShowRefreshLoading();
    void uiHideRefreshLoading();
    void renderUIWithData(T item);
}
