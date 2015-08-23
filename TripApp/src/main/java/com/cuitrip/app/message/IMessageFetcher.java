package com.cuitrip.app.message;

import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.app.base.ListFetchCallback;

/**
 * Created by baziii on 15/8/10.
 */
public interface IMessageFetcher {
    void getMessageList(ListFetchCallback<MessageMode> itemListFetchCallback);
    void getMessageListWithMore(int pattern,ListFetchCallback<MessageMode> itemListFetchCallback);
    void deleteMessage(String id,CtApiCallback callback);
}
