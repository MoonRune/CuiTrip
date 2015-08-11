package com.cuitrip.app.favorite;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab.utils.LogHelper;

import java.util.List;

/**
 * Created by baziii on 15/8/11.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {
    public static final String TAG = "MessageAdapter";
    List<FavoriteMode> mModeList;
    View.OnClickListener mOnClickListener;

    public FavoriteAdapter(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }


    public int getListSize(List list) {
        return list == null ? 0 : list.size();
    }

    public void setModeList(List<FavoriteMode> modeList) {
        int originSize = getListSize(this.mModeList);
        this.mModeList = modeList;
        int nowSize = getListSize(this.mModeList);
        if (originSize <= 0) {
            notifyItemRangeInserted(0, nowSize);
        } else {
            if (originSize < nowSize) {
                notifyItemRangeChanged(0, originSize);
                notifyItemRangeInserted(originSize, nowSize - originSize);
            } else {
                notifyItemRangeChanged(0, nowSize);
            }
        }
    }

    public void appendModeList(List<FavoriteMode> modeList) {
        if (this.mModeList == null || this.mModeList.isEmpty()) {
            setModeList(modeList);
        } else {
            int size = this.mModeList.size();
            this.mModeList.addAll(modeList);
            notifyItemRangeInserted(size, getListSize(modeList));
        }
    }

    public void remove(FavoriteMode messageMode) {
        int index = mModeList.indexOf(messageMode);
        LogHelper.e(TAG, " remove " + index);
        if (index >= 0) {
            mModeList.remove(messageMode);
            notifyItemRemoved(index);
        }
    }


    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(FavoriteViewHolder.RES, parent, false);
        FavoriteViewHolder viewHolder = new FavoriteViewHolder(view, mOnClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        holder.render(mModeList.get(position));
    }

    @Override
    public int getItemCount() {
        return mModeList == null ? 0 : mModeList.size();
    }
}

