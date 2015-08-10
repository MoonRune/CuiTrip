package com.cuitrip.app.message;

import android.view.View;
import android.widget.TextView;

import com.cuitrip.base.RecycleViewHolder;
import com.cuitrip.service.R;

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

    public MessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void render(MessageMode messageMode) {
        mMessageName.setText(messageMode.getName());
        mMessageTime.setText(messageMode.getDate());

    }
}
