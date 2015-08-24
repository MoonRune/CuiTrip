package com.cuitrip.app.message;

import com.cuitrip.model.MessageServerItem;
import com.lab.utils.Utils;

/**
 * Created by baziii on 15/8/10.
 */
public class MessageMode implements java.io.Serializable {
    protected String name;
    protected String id;
    protected String date;
    protected String content;


    //是否正在左滑
    protected boolean isLeft = false;

    public MessageMode(String name, String id, String date, String data) {
        this.name = name;
        this.id = id;
        this.date = date;
        this.content = data;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIsLeft(boolean isLeft) {
        this.isLeft = isLeft;
    }

    public static MessageMode getInstance(MessageServerItem item) {
        return new MessageMode(item.getTitle(), item.getMessageId(),
                Utils.getMsToSeconds(item.getGmtCreated()), item.getContent()
              );
    }
}
