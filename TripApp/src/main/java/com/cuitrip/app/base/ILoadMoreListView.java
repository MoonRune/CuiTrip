package com.cuitrip.app.base;

import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public interface ILoadMoreListView<T> extends IRefreshListView<T> {
    void uiShowLoadMore();
    void uiHideLoadMore();
    void renderUIWithAppendData(List<T> items);
}
