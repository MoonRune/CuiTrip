package com.cuitrip.app.message;

import com.cuitrip.app.base.ILoadMoreView;

/**
 * Created by baziii on 15/8/10.
 */
public interface IMessageView extends ILoadMoreView<MessageMode> {
    void jumpMessage(MessageMode messageMode);
    void moveLeftMessage(MessageMode messageMode);
}
