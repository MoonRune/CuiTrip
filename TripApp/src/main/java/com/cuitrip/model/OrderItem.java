package com.cuitrip.model;


import java.io.Serializable;

public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1523183235771740008L;
    public static final int STATUS_WAIT_COFIRM = 1;
    public static final int STATUS_WAIT_PAY = 2;
    public static final int STATUS_WAIT_START = 3;
    public static final int STATUS_WAIT_END = 4;
    public static final int STATUS_WAIT_COMMENT = 5;
    public static final int STATUS_OVER = 6;
    public static final int STATUS_UNVALIABLE = 7;
//    public enum OrderStatus {
//        CREATED(1), CONFIRMED(2), PAYED(8), WILL_BEGIN(3),BEGIN(4),END(5),CANCEL(6),CLOSED(7);
//        private int status;
//        private OrderStatus(int status){
//            this.status = status;
//        }
//        public int getStatus(){
//            return status;
//        }
//
//    }


    //    "oid": "9_20150626221150_14", //createOrder mofifyOrder 中为orderId
//            "status": "7", //1:订单已生成
//            "statusContent": "订单已关闭",
//            "type": "0","comment":"真心好玩","commentScore":5
//            "sid": "9",
//            "insiderId": "13",
//            "userNick": "Andy",
//            "headPic": "http://cuitrip.oss-cn-shenzhen.aliyuncs.com/14_1435329624387",
//            "insiderName": "Andy",
//            "insiderSign": "再改一句",
//            "travellerId": "14",
//            "paymentWay": "1",
//            "moneyType": "CNY",
//            "serviceName": "bobby带你看花莲老火车",
//            "serviceDate": "2015-06-29 00:00:00",
//            "servicePIC": "http://cuitrip.oss-cn-shenzhen.aliyuncs.com/13_1434945983861",
//            "serviceAddress": "花莲",
//            "orderPrice": "0",
//            "servicePrice": "200",
//            "buyerNum": "1",
//            "extInfo": "",
//            "gmtCreated": "2015-06-26 22:11:50.0",
//            "gmtModified": "2015-06-26 22:38:56.0"
    private String oid; //": "9_20150626221150_14",
    private int status; //: "7",
    private String statusContent; //: "订单已关闭",
    private String type; //": "0",
    private String sid; //": "9",
    private String insiderId; //": "13",
    private String userNick; //": "Andy",
    private String headPic; //": "http://cuitrip.oss-cn-shenzhen.aliyuncs.com/14_1435329624387",
    private String insiderName; //": "Andy",
    private String insiderSign; //": "再改一句",
    private String travellerId; //": "14",
    private String travellerName; //": "Andy",
    private String paymentWay; //": "1",
    private String serviceName; //": "bobby带你看花莲老火车",
    private String serviceDate; //": "2015-06-29 00:00:00", TODO 啥？？订单时间吗
    private String servicePIC; //": "http://cuitrip.oss-cn-shenzhen.aliyuncs.com/13_1434945983861",
    private String serviceAddress; //": "花莲",
    private String orderPrice; //": "0",
    private String payCurrency;
    private String servicePrice; //": "200",
    private String moneyType; //": "CNY",
    private String buyerNum; //": "1",
    private String extInfo; //": "",
    private String gmtCreated; //": "2015-06-26 22:11:50.0",
    private String gmtModified; //": "2015-06-26 22:38:56.0"
    private String comment;
    private String score;
    private String priceType;
    private String feeInclude;
    private String feeExclude;
    private String meetingPlace;
    private String invalidReason;
    private String lat;
    private String lng;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getFeeInclude() {
        return feeInclude;
    }

    public void setFeeInclude(String feeInclude) {
        this.feeInclude = feeInclude;
    }

    public String getFeeExclude() {
        return feeExclude;
    }

    public void setFeeExclude(String feeExclude) {
        this.feeExclude = feeExclude;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }

    public String getPriceType() {
        return priceType;
    }

    public boolean isDiscount() {
        return "2".equals(paymentWay);
    }

    public boolean isPricePerMan() {
        return "1".equals(priceType);
    }

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getCommentScore() {
        try {
            return Float.valueOf(score);
        } catch (Exception e) {
            return -1;
        }
    }

    public void setCommentScore(String commentScore) {
        this.score = commentScore;
    }

    public boolean isClosed() {
        return (status == 6) || (status == 7);
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * createOrder接口中为orderId
     *
     * @param orderId
     */
    public void setOrderId(String orderId) {
        this.oid = orderId;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusContent() {
        return statusContent;
    }

    public void setStatusContent(String statusContent) {
        this.statusContent = statusContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getInsiderName() {
        return insiderName;
    }

    public void setInsiderName(String insiderName) {
        this.insiderName = insiderName;
    }

    public String getInsiderSign() {
        return insiderSign;
    }

    public void setInsiderSign(String insiderSign) {
        this.insiderSign = insiderSign;
    }

    public String getTravellerId() {
        return travellerId;
    }

    public void setTravellerId(String travellerId) {
        this.travellerId = travellerId;
    }

    public String getTravellerName() {
        return travellerName;
    }

    public void setTravellerName(String travellerName) {
        this.travellerName = travellerName;
    }

    public String getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(String paymentWay) {
        this.paymentWay = paymentWay;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getServicePIC() {
        return servicePIC;
    }

    public void setServicePIC(String servicePIC) {
        this.servicePIC = servicePIC;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getBuyerNum() {
        return buyerNum;
    }

    public void setBuyerNum(String buyerNum) {
        this.buyerNum = buyerNum;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public String getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(String gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

}
