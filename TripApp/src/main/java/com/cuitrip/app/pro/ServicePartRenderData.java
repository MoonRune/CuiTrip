package com.cuitrip.app.pro;

import android.text.TextUtils;

import com.cuitrip.app.MainApplication;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.utils.LogHelper;
import com.lab.utils.Utils;

/**
 * Created by baziii on 15/8/10.
 */
public class ServicePartRenderData {
    //favorite name
    protected String serviceName;
    protected String orderDate;
    protected String meetLocation;
    protected String orderPeopleSize;
    protected String serviceDuration;
    protected String priceWithCurrency;
    protected String priceInclude;
    protected String priceUninclude;
    protected String unvaliableReason;
    protected String status;
    protected double lat;
    protected double lng;

    public ServicePartRenderData(String serviceName,
                                 String orderDate, String meetLocation, String orderPeopleSize, String serviceDuration, String priceWithCurrency, String priceInclude,
                                 String priceUninclude,
                                 double lat, double lng,
                                 String status) {
        this.serviceName = serviceName;
        this.orderDate = orderDate;
        this.meetLocation = meetLocation;
        this.orderPeopleSize = orderPeopleSize;
        this.serviceDuration = serviceDuration;
        this.priceWithCurrency = priceWithCurrency;
        this.priceInclude = priceInclude;
        this.priceUninclude = priceUninclude;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUnvaliableReason() {
        return unvaliableReason;
    }

    public void setUnvaliableReason(String unvaliableReason) {
        this.unvaliableReason = unvaliableReason;
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
        LogHelper.e("getOrderDateText", orderItem.getServiceDate() + " to " + Utils.getMsToD(orderItem.getServiceDate()));
        return Utils.getMsToD(orderItem.getServiceDate());
    }

    public static String getOrderPeopleSizeText(OrderItem orderItem) {
        return PlatformUtil.getInstance().getString(R.string.per_man_with_num_above, orderItem.getBuyerNum());
    }

    public static String getOrderDurationText(OrderItem orderItem) {
        return PlatformUtil.getInstance().getString(R.string.per_hour_with_num_above, orderItem.getServiceTime());
    }


    public static String getOrderPriceWithCurrencyText(OrderItem orderItem) {
        UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
        if (userInfo != null) {
            try {
                if (userInfo.getUid().equals(orderItem.getInsiderId())) {
                    return orderItem.getMoneyType() + " " + orderItem.getServicePrice();
                }
            } catch (Exception e) {
            }
        }
        return orderItem.getPayCurrency() + " " + orderItem.getOrderPrice();
    }

    public static String getStatusText(OrderItem orderItem) {
        switch (orderItem.getStatus()) {
            case OrderItem.STATUS_WAIT_COFIRM:
                return PlatformUtil.getInstance().getString(R.string.order_status_wait_confirm);
            case OrderItem.STATUS_WAIT_PAY:
                return PlatformUtil.getInstance().getString(R.string.order_status_wait_pay);
            case OrderItem.STATUS_WAIT_START:
                return PlatformUtil.getInstance().getString(R.string.order_status_wait_start);
            case OrderItem.STATUS_WAIT_END:
                return PlatformUtil.getInstance().getString(R.string.order_status_wait_end);
            case OrderItem.STATUS_WAIT_COMMENT:
                return PlatformUtil.getInstance().getString(R.string.order_status_wait_comment);
            case OrderItem.STATUS_OVER:
                return PlatformUtil.getInstance().getString(R.string.order_status_over);
            case OrderItem.STATUS_UNVALIABLE:
                return PlatformUtil.getInstance().getString(R.string.order_status_unavaliable);
            default:
                return orderItem.getStatusContent();
        }
    }

    public static String getOrderMeet(OrderItem orderItem) {
        return orderItem.getMeetingPlace();
    }

    public static String getOrderPriceInclude(OrderItem orderItem) {
        return orderItem.getFeeInclude();
    }

    public static String getOrderPriceUninclude(OrderItem orderItem) {
        return orderItem.getFeeExclude();
    }

    public static String getUnvaliableReason(OrderItem orderItem) {
        if (TextUtils.isEmpty(orderItem.getInvalidReason())) {
            return PlatformUtil.getInstance().getString(R.string.no_date);
        }
        return orderItem.getInvalidReason();
    }

    public static double getLat(OrderItem orderItem) {

        try {
            return Double.valueOf(orderItem.getLat());
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getLng(OrderItem orderItem) {
        try {
            return Double.valueOf(orderItem.getLng());
        } catch (Exception e) {
            return 0;
        }
    }

    public static ServicePartRenderData getInstance(OrderItem orderItem) {

        ServicePartRenderData result = new ServicePartRenderData(orderItem.getServiceName(),
                getOrderDateText(orderItem)
                , getOrderMeet(orderItem),
                getOrderPeopleSizeText(orderItem),
                getOrderDurationText(orderItem),
                getOrderPriceWithCurrencyText(orderItem), getOrderPriceInclude(orderItem), getOrderPriceUninclude(orderItem),
                getLat(orderItem), getLng(orderItem),
                getStatusText(orderItem)
        );
        return result;
    }
}
