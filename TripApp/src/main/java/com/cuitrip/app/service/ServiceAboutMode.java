package com.cuitrip.app.service;

import com.cuitrip.model.ServiceDetail;
import com.cuitrip.model.ServiceStatistic;
import com.lab.utils.Utils;

/**
 * Created by baziii on 15/8/14.
 */
public class ServiceAboutMode {
    private CharSequence serviceAva;
    private CharSequence serviceName;
    private CharSequence serviceAddress;
    private CharSequence serviceCreateDate;

    private CharSequence wholeVisitAmount;
    private CharSequence todayVisitAmout;
    private CharSequence likedPeople;

    private CharSequence wholeVisitPeopleSize;
    private CharSequence wholeOrderAmount;
    private CharSequence moneyInCome;

    public CharSequence getServiceAva() {
        return serviceAva;
    }

    public void setServiceAva(CharSequence serviceAva) {
        this.serviceAva = serviceAva;
    }

    public CharSequence getServiceName() {
        return serviceName;
    }

    public void setServiceName(CharSequence serviceName) {
        this.serviceName = serviceName;
    }

    public CharSequence getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(CharSequence serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public CharSequence getServiceCreateDate() {
        return serviceCreateDate;
    }

    public void setServiceCreateDate(CharSequence serviceCreateDate) {
        this.serviceCreateDate = serviceCreateDate;
    }

    public CharSequence getWholeVisitAmount() {
        return wholeVisitAmount;
    }

    public void setWholeVisitAmount(CharSequence wholeVisitAmount) {
        this.wholeVisitAmount = wholeVisitAmount;
    }

    public CharSequence getTodayVisitAmout() {
        return todayVisitAmout;
    }

    public void setTodayVisitAmout(CharSequence todayVisitAmout) {
        this.todayVisitAmout = todayVisitAmout;
    }

    public CharSequence getLikedPeople() {
        return likedPeople;
    }

    public void setLikedPeople(CharSequence likedPeople) {
        this.likedPeople = likedPeople;
    }

    public CharSequence getWholeVisitPeopleSize() {
        return wholeVisitPeopleSize;
    }

    public void setWholeVisitPeopleSize(CharSequence wholeVisitPeopleSize) {
        this.wholeVisitPeopleSize = wholeVisitPeopleSize;
    }

    public CharSequence getWholeOrderAmount() {
        return wholeOrderAmount;
    }

    public void setWholeOrderAmount(CharSequence wholeOrderAmount) {
        this.wholeOrderAmount = wholeOrderAmount;
    }

    public CharSequence getMoneyInCome() {
        return moneyInCome;
    }

    public void setMoneyInCome(CharSequence moneyInCome) {
        this.moneyInCome = moneyInCome;
    }

    public ServiceAboutMode(CharSequence serviceAva, CharSequence serviceName, CharSequence serviceAddress, CharSequence serviceCreateDate, CharSequence wholeVisitAmount, CharSequence todayVisitAmout, CharSequence likedPeople, CharSequence wholeVisitPeopleSize, CharSequence wholeOrderAmount, CharSequence moneyInCome) {
        this.serviceAva = serviceAva;
        this.serviceName = serviceName;
        this.serviceAddress = serviceAddress;
        this.serviceCreateDate = serviceCreateDate;
        this.wholeVisitAmount = wholeVisitAmount;
        this.todayVisitAmout = todayVisitAmout;
        this.likedPeople = likedPeople;
        this.wholeVisitPeopleSize = wholeVisitPeopleSize;
        this.wholeOrderAmount = wholeOrderAmount;
        this.moneyInCome = moneyInCome;
    }

    public static ServiceAboutMode getInstance(ServiceDetail orderItem,ServiceStatistic serviceStatistic) {
        ServiceAboutMode  result = new ServiceAboutMode(
                orderItem.getServiceInfo().getBackPic(),
                orderItem.getServiceInfo().getName(),
                orderItem.getServiceInfo().getAddress(),
                Utils.getMsToD(orderItem.getServiceInfo().getGmtCreated()),
                "wholevisit",
                "todayvisit",
                "liked",
                "whoevisitpeople",
                "ordernum",
                "money"
        );
        return result;
    }
}
