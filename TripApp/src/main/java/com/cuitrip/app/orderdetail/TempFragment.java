package com.cuitrip.app.orderdetail;

import android.net.Uri;

import com.cuitrip.app.MainApplication;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by baziii on 15/8/12.
 */
public class TempFragment extends ConversationListFragment {
    public TempFragment() {
        Uri uri = Uri.parse("rong://" + MainApplication.getInstance().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")
                .build();
        setUri(uri);

    }
}
