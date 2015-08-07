package com.cuitrip.conversation.rong;

import com.cuitrip.base.ListFetchCallback;
import com.cuitrip.conversation.ConversationItem;
import com.cuitrip.conversation.IConversationsFetcher;
import com.lab.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by baziii on 15/8/7.
 */
public class ConversationFetcherRong implements IConversationsFetcher {
    @Override
    public void getConversations(final ListFetchCallback<ConversationItem> itemListFetchCallback) {

        RongIM.getInstance().getRongIMClient().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                List<ConversationItem> result = new ArrayList<>();
                if (conversations != null) {
                    for (Conversation conversation : conversations) {
                        ConversationItem item;
                        LogHelper.e("getLatestMessageId", "" + conversation.getLatestMessageId());
                        LogHelper.e("getConversationTitle", "" + conversation.getConversationTitle());
                        if (conversation.getLatestMessage() instanceof VoiceMessage) {
                            VoiceMessage voiceMessage = (VoiceMessage) conversation.getLatestMessage();
                            item = new ConversationItem(conversation.getTargetId(),
                                    conversation.getSenderUserName(),
                                    "voice",
                                    String.valueOf(conversation.getSentTime())
                            );
                        } else if (conversation.getLatestMessage() instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) conversation.getLatestMessage();
                            item = new ConversationItem(conversation.getTargetId(),
                                    conversation.getSenderUserName(),
                                    textMessage.getContent(),
                                    String.valueOf(conversation.getSentTime())
                            );
                        } else if (conversation.getLatestMessage() instanceof RichContentMessage) {
                            RichContentMessage richContentMessage = (RichContentMessage) conversation.getLatestMessage();
                            item = new ConversationItem(conversation.getTargetId(),
                                    conversation.getSenderUserName(),
                                    richContentMessage.getContent(),
                                    String.valueOf(conversation.getSentTime())
                            );
                        } else {
                            item = new ConversationItem(conversation.getTargetId(),
                                    conversation.getSenderUserName(),
                                    conversation.getLatestMessage() != null ? conversation.getLatestMessage().toString() : "",
                                    String.valueOf(conversation.getSentTime())
                            );
                        }
                        result.add(item);
                    }
                }
                itemListFetchCallback.onSuc(result);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }
}
