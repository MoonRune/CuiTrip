package com.cuitrip.app.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;

import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class MessageAdapter extends BaseSwipeAdapter<MessageViewHolder> {
    List<MessageMode> mModeList;
    View.OnClickListener mOnClickListener;

    public MessageAdapter(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }


    public void setmModeList(List<MessageMode> mModeList) {
        this.mModeList = mModeList;
    }

    public void appendModeList(List<MessageMode> modeList) {
        if (this.mModeList == null || this.mModeList.isEmpty()) {
            setmModeList(modeList);
        } else {
            this.mModeList.addAll(modeList);
        }
    }

    public void removeMessageMode(MessageMode messageMode){
        int index=mModeList.indexOf(messageMode);
        if (index>=0){
            mModeList.remove(messageMode);
            notifyItemRemoved(index);
        }
//        mModeList.remove(messageMode);

    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(MessageViewHolder.RES, null);
        MessageViewHolder viewHolder = new MessageViewHolder(view,mOnClickListener);
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
