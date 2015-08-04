package com.cuitrip.model;

import java.util.List;

public class DialogList {
//    "sid":"1",				// 服务id
//            "insiderId":"1",			// 发现者id
//            "serviceDate": "1434773734",	// 预约时间
//            "peopleNum": "4",			// 游玩人数
//            "orderStatus": "1",	// 订单状态: 1:创建订单 2确认订单 4 订单开始 5 订单结束 6 申请退单 7 订单关闭 8 支付成功
//            "dialog":
    private String sid;
    private String insiderId;
    private String serviceDate;
    private String peopleNum;
    private int orderStatus;
    private List<DialogItem> dialog;

    public boolean isClosed(){
        return (orderStatus == 6) || (orderStatus == 7);
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getInsiderId() {
        return insiderId;
    }

    public void setInsiderId(String insiderId) {
        this.insiderId = insiderId;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(String peopleNum) {
        this.peopleNum = peopleNum;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<DialogItem> getDialog() {
        return dialog;
    }

    public void setDialog(List<DialogItem> dialog) {
        this.dialog = dialog;
    }
}
