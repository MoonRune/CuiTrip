package com.cuitrip.app.service;

/**
 * Created by baziii on 15/8/14.
 */
public class ServiceAboutMode {
    private CharSequence serviceAva;
    private CharSequence serviceName;
    private CharSequence serviceCreateDate;
    private CharSequence todayVisitAmount;
    private CharSequence wholeVisitAmount;
    private CharSequence todayVisitPeople;
    private CharSequence wholeVisitPeople;
    private CharSequence likedPeople;
    private CharSequence wholeOrderAmount;
    private CharSequence payedOrderAmount;
    private CharSequence overOrderAmount;
    private CharSequence cancelOrderAmount;

    public ServiceAboutMode(CharSequence serviceAva, CharSequence serviceName, CharSequence serviceCreateDate, CharSequence todayVisitAmount, CharSequence wholeVisitAmount, CharSequence todayVisitPeople, CharSequence wholeVisitPeople, CharSequence likedPeople, CharSequence wholeOrderAmount, CharSequence payedOrderAmount, CharSequence overOrderAmount, CharSequence cancelOrderAmount) {
        this.serviceAva = serviceAva;
        this.serviceName = serviceName;
        this.serviceCreateDate = serviceCreateDate;
        this.todayVisitAmount = todayVisitAmount;
        this.wholeVisitAmount = wholeVisitAmount;
        this.todayVisitPeople = todayVisitPeople;
        this.wholeVisitPeople = wholeVisitPeople;
        this.likedPeople = likedPeople;
        this.wholeOrderAmount = wholeOrderAmount;
        this.payedOrderAmount = payedOrderAmount;
        this.overOrderAmount = overOrderAmount;
        this.cancelOrderAmount = cancelOrderAmount;
    }

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

    public CharSequence getServiceCreateDate() {
        return serviceCreateDate;
    }

    public void setServiceCreateDate(CharSequence serviceCreateDate) {
        this.serviceCreateDate = serviceCreateDate;
    }

    public CharSequence getTodayVisitAmount() {
        return todayVisitAmount;
    }

    public void setTodayVisitAmount(CharSequence todayVisitAmount) {
        this.todayVisitAmount = todayVisitAmount;
    }

    public CharSequence getWholeVisitAmount() {
        return wholeVisitAmount;
    }

    public void setWholeVisitAmount(CharSequence wholeVisitAmount) {
        this.wholeVisitAmount = wholeVisitAmount;
    }

    public CharSequence getTodayVisitPeople() {
        return todayVisitPeople;
    }

    public void setTodayVisitPeople(CharSequence todayVisitPeople) {
        this.todayVisitPeople = todayVisitPeople;
    }

    public CharSequence getWholeVisitPeople() {
        return wholeVisitPeople;
    }

    public void setWholeVisitPeople(CharSequence wholeVisitPeople) {
        this.wholeVisitPeople = wholeVisitPeople;
    }

    public CharSequence getLikedPeople() {
        return likedPeople;
    }

    public void setLikedPeople(CharSequence likedPeople) {
        this.likedPeople = likedPeople;
    }

    public CharSequence getWholeOrderAmount() {
        return wholeOrderAmount;
    }

    public void setWholeOrderAmount(CharSequence wholeOrderAmount) {
        this.wholeOrderAmount = wholeOrderAmount;
    }

    public CharSequence getPayedOrderAmount() {
        return payedOrderAmount;
    }

    public void setPayedOrderAmount(CharSequence payedOrderAmount) {
        this.payedOrderAmount = payedOrderAmount;
    }

    public CharSequence getOverOrderAmount() {
        return overOrderAmount;
    }

    public void setOverOrderAmount(CharSequence overOrderAmount) {
        this.overOrderAmount = overOrderAmount;
    }

    public CharSequence getCancelOrderAmount() {
        return cancelOrderAmount;
    }

    public void setCancelOrderAmount(CharSequence cancelOrderAmount) {
        this.cancelOrderAmount = cancelOrderAmount;
    }
}
