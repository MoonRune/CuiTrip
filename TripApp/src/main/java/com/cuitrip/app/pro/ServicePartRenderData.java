package com.cuitrip.app.pro;

import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/10.
 */
public class ServicePartRenderData {
    //favorite name
    protected String serviceName;
    protected String orderStatus;
    protected String orderDate;
    protected String meetLocation;
    protected String orderPeopleSize;
    protected String serviceDuration;
    protected String priceWithCurrency;
    protected String priceInclude;
    protected String priceUninclude;

    public ServicePartRenderData(String serviceName, String orderStatus, String orderDate, String meetLocation, String orderPeopleSize, String serviceDuration, String priceWithCurrency, String priceInclude, String priceUninclude) {
        this.serviceName = serviceName;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.meetLocation = meetLocation;
        this.orderPeopleSize = orderPeopleSize;
        this.serviceDuration = serviceDuration;
        this.priceWithCurrency = priceWithCurrency;
        this.priceInclude = priceInclude;
        this.priceUninclude = priceUninclude;
    }

    public String getMeetLocation() {
        return meetLocation;
    }

    public void setMeetLocation(String meetLocation) {
        this.meetLocation = meetLocation;
    }

    public String getPriceInclude() {
        return priceInclude;
    }

    public void setPriceInclude(String priceInclude) {
        this.priceInclude = priceInclude;
    }

    public String getPriceUninclude() {
        return priceUninclude;
    }

    public void setPriceUninclude(String priceUninclude) {
        this.priceUninclude = priceUninclude;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderPeopleSize() {
        return orderPeopleSize;
    }

    public void setOrderPeopleSize(String orderPeopleSize) {
        this.orderPeopleSize = orderPeopleSize;
    }

    public String getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(String serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public String getPriceWithCurrency() {
        return priceWithCurrency;
    }

    public void setPriceWithCurrency(String priceWithCurrency) {
        this.priceWithCurrency = priceWithCurrency;
    }


    public static String getOrderDateText(OrderItem orderItem) {
        return orderItem.getServiceDate();
    }

    public static String getOrderPeopleSizeText(OrderItem orderItem) {
        return orderItem.getBuyerNum();
    }

    public static String getOrderDurationText(OrderItem orderItem) {
        return orderItem.getBuyerNum();
    }


    public static String getOrderPriceWithCurrencyText(OrderItem orderItem) {
        return orderItem.getPayCurrency() + orderItem.getOrderPrice();
    }

    public static String getStatusText(OrderItem orderItem){
        switch (orderItem.getStatus()) {
            case OrderItem.STATUS_WAIT_COFIRM:
                return "等待发现者确认";
            case OrderItem.STATUS_WAIT_PAY:
                return "等待旅行者支付";
            case OrderItem.STATUS_WAIT_START:
                return "等待发现者开始";
            case OrderItem.STATUS_WAIT_END:
                return "旅程进行中";
            case OrderItem.STATUS_WAIT_COMMENT:
                return "等待评价";
            case OrderItem.STATUS_OVER:
                return "旅程结束";
            case OrderItem.STATUS_UNVALIABLE:
                return "预订失效";
            default:
                return orderItem.getStatusContent();
        }
    }
    public static String getOrderLocation(OrderItem orderItem) {
        return "  waitting api add location to orderitem!!";
    }
    public static String getOrderPriceInclude(OrderItem orderItem) {
        return "  waitting api add include to orderitem!!";
    }

    public static String getOrderPriceUninclude(OrderItem orderItem) {
        return "  waitting api add uninclude to orderitem!!";
    }


    public static ServicePartRenderData getInstance(OrderItem orderItem) {

        ServicePartRenderData result = new ServicePartRenderData(orderItem.getServiceName(),
                getStatusText(orderItem), getOrderDateText(orderItem),
                getOrderPeopleSizeText(orderItem),getOrderLocation(orderItem), getOrderDurationText(orderItem),
                getOrderPriceWithCurrencyText(orderItem), getOrderPriceInclude(orderItem),getOrderPriceUninclude(orderItem));
        return result;
    }
}
