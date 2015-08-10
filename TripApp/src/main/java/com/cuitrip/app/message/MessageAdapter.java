package com.cuitrip.app.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab.utils.LogHelper;

import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    public static final String TAG = "MessageAdapter";
    List<MessageMode> mModeList;
    View.OnClickListener mOnClickListener;

    public MessageAdapter(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }


    public int getListSize(List list) {
        return list == null ? 0 : list.size();
    }

    public void setModeList(List<MessageMode> modeList) {
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

    public void appendModeList(List<MessageMode> modeList) {
        if (this.mModeList == null || this.mModeList.isEmpty()) {
            setModeList(modeList);
        } else {
            int size = this.mModeList.size();
            this.mModeList.addAll(modeList);
            notifyItemRangeInserted(size, getListSize(modeList));
        }
    }

    public void removeMessageMode(MessageMode messageMode) {
        int index = mModeList.indexOf(messageMode);
        LogHelper.e(TAG, " remove " + index);
        if (index >= 0) {
            mModeList.remove(messageMode);
            notifyItemRemoved(index);
        }
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(MessageViewHolder.RES, parent,false);
        MessageViewHolder viewHolder = new MessageViewHolder(view, mOnClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.render(mModeList.get(position));
    }

    @Override
    public int getItemCount() {
        return mModeList == null ? 0 : mModeList.size();
    }
}
