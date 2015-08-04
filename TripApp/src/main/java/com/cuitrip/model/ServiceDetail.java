package com.cuitrip.model;

import java.io.Serializable;

public class ServiceDetail implements Serializable{
    private static final long serialVersionUID = -2464737498004712125L;
    private ReviewInfo reviewInfo;
    private UserInfo userInfo;
    private ServiceInfo serviceInfo;

    public ReviewInfo getReviewInfo() {
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
