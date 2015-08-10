package com.cuitrip.app.message;

import com.cuitrip.base.ListFetchCallback;

/**
 * Created by baziii on 15/8/10.
 */
public interface IMessageFetcher {
    void getMessageList(ListFetchCallback<MessageMode> itemListFetchCallback);
    void getMessageListWithMore(String pattern,ListFetchCallback<MessageMode> itemListFetchCallback);
}
