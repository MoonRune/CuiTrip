package com.cuitrip.app.message;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuitrip.app.base.RecycleViewHolder;
import com.cuitrip.service.R;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

import butterknife.InjectView;

/**
 * Created by baziii on 15/8/10.
 */
public class MessageViewHolder extends RecycleViewHolder<MessageMode> {
    public static int RES = R.layout.message_item;
    @InjectView(R.id.ct_message_name)
    TextView mMessageName;
    @InjectView(R.id.ct_message_time)
    TextView mMessageTime;
    @InjectView(R.id.ct_delete)
    View mDelete;
    @InjectView(R.id.ct_swip_layout)
    SwipeLayout mSwipLayout;
    @InjectView(R.id.ct_message_layout)
    LinearLayout mMessageLayout;
    MessageMode messageMode;

    public MessageViewHolder(View itemView, View.OnClickListener mOnClickListener) {
        super(itemView);
        mSwipLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        mSwipLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        mMessageLayout.setOnClickListener(mOnClickListener);
        mDelete.setOnClickListener(mOnClickListener);
    }

    @Override
    public void render(MessageMode messageMode) {
        this.messageMode=messageMode;
        mMessageLayout.setTag(messageMode);
        mDelete.setTag(messageMode);
        mMessageName.setText(messageMode.getName());
        mMessageTime.setText(messageMode.getDate());

    }
}
