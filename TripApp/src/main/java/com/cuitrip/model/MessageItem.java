package com.cuitrip.model;


public class MessageItem {
//    "type":"2", // 消息类型：1系统消息，2下单通知消息 3确认订单消息 4对话通知消息 5 评论通知消息
//            "headPic":"http://****",	// 头像
//            "topic":"阿亮带您环岛游",	//消息title
//            "lastMsg":"好的，到时见",	// 上一条消息
//            "orderId":"11111_22222",	// 订单号，根据订单号跳转的聊天详情页

    private String type;
    private String headPic;
    private String topic;
    private String lastMsg;
    private String orderId;
    private String gmtCreated;
    private String nick;

    public boolean isSystemMsg(){
        return "1".equals(type);
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(String gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
