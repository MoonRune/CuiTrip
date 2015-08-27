package com.cuitrip.app.base;

import android.view.View;

/**
 * Created by baziii on 15/8/11.
 */
public interface PartViewHolder<T> {
    void build(View view);
    void render(T t);
}
