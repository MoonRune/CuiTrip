package com.cuitrip.model;


public class RecommendItem {
//    "sid": "231", //服务ID
//            "serviceName": "阿亮带你看妈祖绕境", //旅程名称
//            "serviceAddress": "台湾彰化县", //旅程所在地
//            "headPic": "http://alicdn.aliyun.com/pic1.jpg", //发现者头像
//            "userNick": "阿亮"， // 发现者昵称
//            "servicePicUrl": "http://******"， // 发现者昵称

    private String sid;
    private String serviceName;
    private String serviceAddress;
    private String headPic;
    private String userNick;
    private String servicePicUrl;
    private String authorCarrer;//todo api
    private boolean isLiked;

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public String getAuthorCarrer() {
        return authorCarrer;
    }

    public void setAuthorCarrer(String authorCarrer) {
        this.authorCarrer = authorCarrer;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getServicePicUrl() {
        return servicePicUrl;
    }

    public void setServicePicUrl(String servicePicUrl) {
        this.servicePicUrl = servicePicUrl;
    }
}
