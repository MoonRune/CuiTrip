package com.lab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected List<T> mItems;
    protected LayoutInflater mInflater;
    protected int mResource;

    public BaseListAdapter(Context context, int resourceId) {
        mResource = resourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<T> items) {
        mItems = items;
    }

    public List<T> getData() {
        return mItems;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public T getItem(int i) {
        if (i < 0 || mItems == null || i >= mItems.size()) {
            return null;
        }
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (null == view) {
            view = mInflater.inflate(mResource, null);
            view.setTag(view2Holder(view));
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        bindView(viewHolder, mItems.get(i), i);
        return view;
    }

    protected abstract ViewHolder view2Holder(View view);

    protected abstract void bindView(ViewHolder holder, T item, int position);
}
