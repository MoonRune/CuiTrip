package com.cuitrip.app.conversation;

import android.view.View;
import android.widget.TextView;

import com.cuitrip.app.base.RecycleViewHolder;
import com.cuitrip.service.R;
import com.lab.utils.ImageHelper;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by baziii on 15/8/7.
 */
public class ConversationsViewHolder extends RecycleViewHolder<ConversationItem> {
    public static int RES = R.layout.conversations_item;
    @InjectView(R.id.ct_message_img)
    CircleImageView ctMessageImg;
    @InjectView(R.id.ct_message_name)
    TextView ctMessageName;
    @InjectView(R.id.ct_message_content)
    TextView ctMessageContent;
    @InjectView(R.id.ct_message_time)
    TextView ctMessageTime;
    ConversationsPresent present;
    ConversationItem item;
    @InjectView(R.id.ct_message_service)
    TextView ctMessageService;
    @InjectView(R.id.ct_message_notification_count)
    TextView ctMessageNotificationCount;

    public ConversationsViewHolder(View itemView, final ConversationsPresent present) {
        super(itemView);
        this.present = present;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.onConversationClick(item);
            }
        });
    }

    public void render(ConversationItem item) {
        this.item = item;
        ImageHelper.displayPersonImage(item.getAva(), ctMessageImg, null);
        ctMessageName.setText(item.getName());
        ctMessageContent.setText(item.getLastWords());
        ctMessageTime.setText(item.getTime());
        ctMessageService.setText(item.getServiceName());
        ctMessageNotificationCount.setVisibility(item.getUnreadCount()>0?View.VISIBLE:View.INVISIBLE);
        ctMessageNotificationCount.setText(String.valueOf(item.getUnreadCount()));
    }

}
