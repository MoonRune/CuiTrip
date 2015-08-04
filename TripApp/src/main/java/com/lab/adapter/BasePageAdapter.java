package com.lab.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.List;

public class BasePageAdapter<T> extends PagerAdapter {
    protected List<T> mData;

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        if(mData != null){
            return mData.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
