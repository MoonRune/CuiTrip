package com.cuitrip.app.conversation;

/**
 * Created by baziii on 15/8/7.
 */
public class ConversationItem {
    private String id;
    private String name;
    private int unreadCount;
    private String serviceName;
    private String lastWords;
    private String time;
    private String ava;
    private String orderId;

    public ConversationItem(String id, String name, int unreadCount, String serviceName, String lastWords, String time, String ava, String orderId) {
        this.id = id;
        this.name = name;
        this.unreadCount = unreadCount;
        this.serviceName = serviceName;
        this.lastWords = lastWords;
        this.time = time;
        this.ava = ava;
        this.orderId = orderId;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getAva() {
        return ava;
    }

    public void setAva(String ava) {
        this.ava = ava;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setLastWords(String lastWords) {
        this.lastWords = lastWords;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastWords() {
        return lastWords;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "ConversationItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", lastWords='" + lastWords + '\'' +
                ", time='" + time + '\'' +
                ", ava='" + ava + '\'' +
                '}';
    }
}
