package com.cuitrip.model;


public class ReviewListItem {
//    "No": "1", //列表中旅程序号
//            "cid": "231", //订单ID
//            "travellerId": "25", //旅行者id
//            "insiderHeadPic": "http://alicdn.aliyun.com/pic1.jpg", //发现者头像
//            "insiderNickName": "阿亮", // 发现者昵称
//            "gmtCreated": "2015-08-09", //评论日期
//            "content": "终于看到了《练习曲》中的那个场景，很真实，这才是真正的台湾，"
    private String No; //": "1", //列表中旅程序号
    private String cid; //": "231", //订单ID
    private String travellerId; //": "25", //旅行者id
    private String insiderHeadPic; //": "http://alicdn.aliyun.com/pic1.jpg", //发现者头像
    private String insiderNickName; //": "阿亮", // 发现者昵称
    private String gmtCreated; //": "2015-08-09", //评论日期
    private String content; //": "终于看到了《练习曲》中的那个场景，很真实，这才是真正的台湾，"

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getTravellerId() {
        return travellerId;
    }

    public void setTravellerId(String travellerId) {
        this.travellerId = travellerId;
    }

    public String getInsiderHeadPic() {
        return insiderHeadPic;
    }

    public void setInsiderHeadPic(String insiderHeadPic) {
        this.insiderHeadPic = insiderHeadPic;
    }

    public String getInsiderNickName() {
        return insiderNickName;
    }

    public void setInsiderNickName(String insiderNickName) {
        this.insiderNickName = insiderNickName;
    }

    public String getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(String gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
