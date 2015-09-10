package com.cuitrip.app.conversation;

import com.cuitrip.app.base.ListFetchCallback;

/**
 * Created by baziii on 15/8/7.
 */
public interface IConversationsFetcher {
    void getConversations(ListFetchCallback<ConversationItem> itemListFetchCallback);

    void getConversationsMore(final ListFetchCallback<ConversationItem> itemListFetchCallback, int from);
}
