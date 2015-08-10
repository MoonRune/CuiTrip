package com.cuitrip.app.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    List<MessageMode> modeList;

    public void setModeList(List<MessageMode> modeList) {
        this.modeList = modeList;
    }

    public void appendModeList(List<MessageMode> modeList) {
        if (this.modeList == null) {
            setModeList(modeList);
        } else {
            this.modeList.addAll(modeList);
        }
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(MessageViewHolder.RES, null);
        MessageViewHolder viewHolder = new MessageViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.render(modeList.get(position));
    }

    @Override
    public int getItemCount() {
        return modeList == null ? 0 : modeList.size();
    }
}
