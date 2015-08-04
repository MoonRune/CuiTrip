package com.cuitrip.model;

public class DialogItem {
//    "type":"4",
//            "content":"你好，我是阿亮",
//            "headPic":"http://*****.jpg",		// 阿亮的头像
//            "gmtCreated":"2015-06-20 12:01:00",
//            "from":"1",				// 消息发送者id
//            "to":"10"				// 消息接收者id

    private String type;
    private String content;
    private String headPic;
    private String gmtCreated;
    private String from;
    private String to;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(String gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
