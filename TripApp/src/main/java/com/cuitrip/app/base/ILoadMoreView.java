package com.cuitrip.app.base;

/**
 * Created by baziii on 15/8/10.
 */
public interface ILoadMoreView<T> extends IRefreshView<T> {
    void uiShowLoadMore();
    void uiHideLoadMore();
}
