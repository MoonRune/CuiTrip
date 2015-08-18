package com.cuitrip.app.message;

import com.cuitrip.app.base.ILoadMoreListView;

/**
 * Created by baziii on 15/8/10.
 */
public interface IMessageListView extends ILoadMoreListView<MessageMode> {
    void jumpMessage(MessageMode messageMode);
    void deleteMessage(MessageMode messageMode);
    int getSize();
}
