package com.cuitrip.model;

import java.util.List;

public class RecommendOutData {
//    "total": "12", //总的结果数
//            "num": "2", //当前请求返回结果数
//            "start": "10", // 开始位置
//            "lists": [{
//        "sid": "231", //服务ID
//                "serviceName": "阿亮带你看妈祖绕境", //旅程名称
//                "serviceAddress": "台湾彰化县", //旅程所在地
//                "headPic": "http://alicdn.aliyun.com/pic1.jpg", //发现者头像
//                "userNick": "阿亮"， // 发现者昵称
//        "servicePicUrl": "http://******"， // 发现者昵称
//    }]
    private int total;
    private int num;
    private int start;
    private List<RecommendItem> lists;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public List<RecommendItem> getLists() {
        return lists;
    }

    public void setLists(List<RecommendItem> lists) {
        this.lists = lists;
    }
}
