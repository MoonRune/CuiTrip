package com.cuitrip.app.pay;

/**
 * Created by baziii on 15/8/15.
 */
public class PayOrderMode {
    private String serviceAva;
    private String serviceName;
    private String orderDate;
    private String buyerSize;
    private String servicePrice;//订单费用不优惠的总
    private String serviceCurrency;////订单费用货币单位
    private boolean discounted;
    private String discountDesc;//优惠的描述
    private String discountedPrice;//优惠的金额
    private String finalPrice;//最终费用金额

    public String getServiceAva() {
        return serviceAva;
    }

    public void setServiceAva(String serviceAva) {
        this.serviceAva = serviceAva;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getBuyerSize() {
        return buyerSize;
    }

    public void setBuyerSize(String buyerSize) {
        this.buyerSize = buyerSize;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getServiceCurrency() {
        return serviceCurrency;
    }

    public void setServiceCurrency(String serviceCurrency) {
        this.serviceCurrency = serviceCurrency;
    }

    public boolean isDiscounted() {
        return discounted;
    }

    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    public String getDiscountDesc() {
        return discountDesc;
    }

    public void setDiscountDesc(String discountDesc) {
        this.discountDesc = discountDesc;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public PayOrderMode(String serviceAva, String serviceName, String orderDate, String buyerSize, String servicePrice, String serviceCurrency, boolean discounted, String discountDesc, String discountedPrice, String finalPrice) {
        this.serviceAva = serviceAva;
        this.serviceName = serviceName;
        this.orderDate = orderDate;
        this.buyerSize = buyerSize;
        this.servicePrice = servicePrice;
        this.serviceCurrency = serviceCurrency;
        this.discounted = discounted;
        this.discountDesc = discountDesc;
        this.discountedPrice = discountedPrice;
        this.finalPrice = finalPrice;
    }
}
