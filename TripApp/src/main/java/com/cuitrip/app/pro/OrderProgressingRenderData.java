package com.cuitrip.app.pro;

import com.cuitrip.app.MainApplication;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;

/**
 * Created by baziii on 15/8/10.
 */
public class OrderProgressingRenderData {
    protected String travelName;
    protected String travelAva;

    protected String finderName;
    protected String finderAva;

    protected String serviceName;
    protected String serviceAddress;

    public String getTravelName() {
        return travelName;
    }

    public void setTravelName(String travelName) {
        this.travelName = travelName;
    }

    public String getTravelAva() {
        return travelAva;
    }

    public void setTravelAva(String travelAva) {
        this.travelAva = travelAva;
    }

    public String getFinderName() {
        return finderName;
    }

    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }

    public String getFinderAva() {
        return finderAva;
    }

    public void setFinderAva(String finderAva) {
        this.finderAva = finderAva;
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

    public OrderProgressingRenderData(String travelName, String travelAva, String finderName, String finderAva, String serviceName, String serviceAddress) {

        this.travelName = travelName;
        this.travelAva = travelAva;
        this.finderName = finderName;
        this.finderAva = finderAva;
        this.serviceName = serviceName;
        this.serviceAddress = serviceAddress;
    }

    public static OrderProgressingRenderData getInstance(OrderItem orderItem) {
        UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
        String finderAva;
        String travelAva;
        if (userInfo.getUid().equals(orderItem.getTravellerId())) {
            if (userInfo.isTravel()) {
                travelAva = userInfo.getHeadPic();
                finderAva = orderItem.getHeadPic();
            } else {
                //todo
                throw new RuntimeException("user with finder status order  see travel order !!!");
            }
        } else {
            if (userInfo.isTravel()) {
                //todo
                throw new RuntimeException("user with traveller status order  see finder order !!!");

            } else {
                finderAva = userInfo.getHeadPic();
                travelAva = orderItem.getHeadPic();
            }
        }
        return new OrderProgressingRenderData(orderItem.getTravellerName(), travelAva,
                orderItem.getInsiderName(), finderAva, orderItem.getServiceName(), orderItem.getServiceAddress());
    }
}
