package com.cuitrip.model;

/**
 * Created on 7/15.
 */
public class ServiceStatistic {
//    "availableDate": ["1438012800000"],
//            "sid": "40"
    private String likeNum;
    private String orderNum;
    private String paidNum;
    private String orderDoneNum;
    private String cancelNum;

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getPaidNum() {
        return paidNum;
    }

    public void setPaidNum(String paidNum) {
        this.paidNum = paidNum;
    }

    public String getOrderDoneNum() {
        return orderDoneNum;
    }

    public void setOrderDoneNum(String orderDoneNum) {
        this.orderDoneNum = orderDoneNum;
    }

    public String getCancelNum() {
        return cancelNum;
    }

    public void setCancelNum(String cancelNum) {
        this.cancelNum = cancelNum;
    }
}

