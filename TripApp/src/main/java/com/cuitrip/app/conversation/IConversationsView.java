package com.cuitrip.app.conversation;

import java.util.List;

/**
 * Created by baziii on 15/8/7.
 */
public interface IConversationsView {
    void showRefreshLoading();
    void hideRefreshLoading();
    void disableEvent();
    void enableEvent();
    void showLoadMore();
    void hideLoadMore();
    void jumpConversation(ConversationItem item);
    void removeItem(ConversationItem item);
    void refreshMessage(List<ConversationItem> items);
}
