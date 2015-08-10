package com.cuitrip.app.base;

import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public interface IRefreshView<T> extends IView {
    void uiShowRefreshLoading();
    void uiHideRefreshLoading();
    void renderUIWithData(List<T> items);
}
