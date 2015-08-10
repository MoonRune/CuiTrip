package com.cuitrip.conversation;

import com.cuitrip.app.base.ListFetchCallback;

/**
 * Created by baziii on 15/8/7.
 */
public interface IConversationsFetcher {
     void getConversations(ListFetchCallback<ConversationItem> itemListFetchCallback);
}
