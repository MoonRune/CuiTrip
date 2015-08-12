package com.cuitrip.app.base;

/**
 * Created by baziii on 15/8/7.
 */
public interface CtApiCallback {
    void onSuc();
    void onFailed(CtException throwable);
}
