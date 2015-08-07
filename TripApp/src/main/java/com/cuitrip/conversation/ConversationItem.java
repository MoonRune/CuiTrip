package com.cuitrip.conversation;

/**
 * Created by baziii on 15/8/7.
 */
public class ConversationItem {
    private String id;
    private String name;
    private String lastWords;
    private String time;

    public ConversationItem(String id, String name,  String lastWords, String time) {
        this.id = id;
        this.name = name;
        this.lastWords = lastWords;
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
                ", lastWords='" + lastWords + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
