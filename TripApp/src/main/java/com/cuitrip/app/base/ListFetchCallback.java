package com.cuitrip.app.base;

import java.util.List;

/**
 * Created by baziii on 15/8/7.
 */
public interface ListFetchCallback<T> {
    void onSuc(List<T> t);
    void onFailed(Throwable throwable);
}
