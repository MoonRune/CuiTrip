package com.cuitrip.app.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by baziii on 15/8/7.
 */
public abstract class RecycleViewHolder<T> extends RecyclerView.ViewHolder{
    public RecycleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public abstract void render(T t);

}
