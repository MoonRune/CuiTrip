package com.cuitrip.app.base;

/**
 * Created by baziii on 15/8/10.
 */
public interface IRefreshLoadMoreJumpDeleteView<T> extends ILoadMoreView<T> {
    void jump(T messageMode);

    void delete(T messageMode);
}