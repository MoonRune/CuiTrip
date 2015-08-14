package com.cuitrip.app.base;

/**
 * Created by baziii on 15/8/7.
 */
public interface CtFetchCallback<T> {
    void onSuc(T t);
    void onFailed(CtException throwable);
}
