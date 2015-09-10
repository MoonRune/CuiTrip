package com.cuitrip.app.conversation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by baziii on 15/8/7.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationsViewHolder> {
    List<ConversationItem> datas;

    public ConversationAdapter(ConversationsPresent present) {
        this.present = present;
    }

    ConversationsPresent present;

    public void setDatas(List<ConversationItem> datas) {
        this.datas = datas;
    }

    public void append(List<ConversationItem> datas) {
        if (this.datas == null) {
            this.datas = datas;
        } else {
            this.datas.addAll(datas);
        }
    }

    @Override
    public ConversationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(ConversationsViewHolder.RES, parent, false);
        ConversationsViewHolder viewHolder = new ConversationsViewHolder(view, present);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationsViewHolder holder, int position) {
        holder.render(datas.get(position));

    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
