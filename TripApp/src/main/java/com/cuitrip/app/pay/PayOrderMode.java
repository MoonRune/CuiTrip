package com.cuitrip.app.pay;

import com.cuitrip.model.DiscountItem;
import com.cuitrip.model.OrderItem;
import com.lab.utils.Utils;

import java.util.List;

/**
 * Created by baziii on 15/8/15.
 */
public class PayOrderMode {
    private String serviceAva;
    private String serviceName;
    private String orderDate;
    private String buyerSize;
    private String serviceNormalPrice;//订单费用不优惠的总
    private String serviceCurrency;////订单费用货币单位
//    private String discountDesc;//优惠的描述
//    private String discountedPrice;//优惠的金额
//    private String finalPrice;//最终费用金额

    private DiscountItem discount;

    private List<DiscountItem> discountItems;

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

    public String getServiceNormalPrice() {
        return serviceNormalPrice;
    }

    public void setServiceNormalPrice(String servicePrice) {
        this.serviceNormalPrice = servicePrice;
    }

    public String getOrderCurrency() {
        return serviceCurrency;
    }

    public void setServiceCurrency(String serviceCurrency) {
        this.serviceCurrency = serviceCurrency;
    }

    public String getServiceCurrency() {
        return serviceCurrency;
    }

    public DiscountItem getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountItem discount) {
        this.discount = discount;
    }

    public List<DiscountItem> getDiscountItems() {
        return discountItems;
    }

    public void setDiscountItems(List<DiscountItem> discountItems) {
        this.discountItems = discountItems;
    }

    public PayOrderMode() {
    }

    public PayOrderMode(String serviceAva, String serviceName, String orderDate, String buyerSize, String servicePrice, String serviceCurrency ,List<DiscountItem>  discountItems) {
        this.serviceAva = serviceAva;
        this.serviceName = serviceName;
        this.orderDate = orderDate;
        this.buyerSize = buyerSize;
        this.serviceNormalPrice = servicePrice;
        this.serviceCurrency = serviceCurrency;
        this.discountItems = discountItems;
    }

    public static PayOrderMode getInstance(OrderItem orderItem,List<DiscountItem> discountItems) {
        PayOrderMode result = new PayOrderMode(
                orderItem.getServicePIC(),
                orderItem.getServiceName(),
                Utils.getMsToD(orderItem.getServiceDate() ) ,
                orderItem.getBuyerNum()+"人",
                orderItem.getOrderPrice(),
                orderItem.getPayCurrency(),
                discountItems
        );
        return result ;
    }
}
