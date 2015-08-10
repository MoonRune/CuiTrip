package com.cuitrip.app.base;

/**
 * Created by baziii on 15/8/7.
 */
public interface FetchCallback {
    void onSuc();
    void onFailed(Throwable throwable);
}
