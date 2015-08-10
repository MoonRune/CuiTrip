package com.cuitrip.base;

import android.view.View;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;

import butterknife.ButterKnife;

/**
 * Created by baziii on 15/8/7.
 */
public abstract class RecycleViewHolder<T> extends BaseSwipeAdapter.BaseSwipeableViewHolder{
    public RecycleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public abstract void render(T t);

}
