package com.cuitrip.app.pro;

/**
 * Created by baziii on 15/8/10.
 */
public class ServicePartRenderData {
    //favorite name
    protected String serviceName;
    protected String orderStatus;
    protected String orderDate;

    protected String orderPeopleSize;
    protected String serviceTime;
    protected String priceWithCurrency;
    protected String priceDesc;

    public String getServiceTime() {
        return serviceTime;
    }

    public String getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        this.priceDesc = priceDesc;
    }

    public ServicePartRenderData(String serviceName, String orderStatus, String orderDate, String orderPeopleSize, String serviceTime, String priceWithCurrency, String priceDesc) {

        this.serviceName = serviceName;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.orderPeopleSize = orderPeopleSize;
        this.serviceTime = serviceTime;
        this.priceWithCurrency = priceWithCurrency;
        this.priceDesc = priceDesc;
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
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getPriceWithCurrency() {
        return priceWithCurrency;
    }

    public void setPriceWithCurrency(String priceWithCurrency) {
        this.priceWithCurrency = priceWithCurrency;
    }
}
