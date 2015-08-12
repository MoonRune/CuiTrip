package com.cuitrip.app.pro;

/**
 * Created by baziii on 15/8/10.
 */
public class OrderPersonRenderData {
    protected String userAva;
    protected String userName;
    protected String userRegisterTime;
    protected String userRealName;
    protected String userBirth;
    protected String userGender;
    protected String userCity;
    protected String userCarrer;
    protected String userHobby;
    protected String userLangeage;
    protected String userSign;
    protected String userPhone;
    protected boolean userPhoneValidated;
    protected String userEmail;
    protected boolean userEmailValidated;
    protected String userIdentity;
    protected boolean userIdentityValidated;

    public OrderPersonRenderData(String userAva, String userName, String userRegisterTime, String userRealName, String userBirth, String userGender, String userCity, String userCarrer, String userHobby, String userLangeage, String userSign, String userPhone, boolean userPhoneValidated, String userEmail, boolean userEmailValidated, String userIdentity, boolean userIdentityValidated) {
        this.userAva = userAva;
        this.userName = userName;
        this.userRegisterTime = userRegisterTime;
        this.userRealName = userRealName;
        this.userBirth = userBirth;
        this.userGender = userGender;
        this.userCity = userCity;
        this.userCarrer = userCarrer;
        this.userHobby = userHobby;
        this.userLangeage = userLangeage;
        this.userSign = userSign;
        this.userPhone = userPhone;
        this.userPhoneValidated = userPhoneValidated;
        this.userEmail = userEmail;
        this.userEmailValidated = userEmailValidated;
        this.userIdentity = userIdentity;
        this.userIdentityValidated = userIdentityValidated;
    }

    public String getUserAva() {
        return userAva;
    }

    public void setUserAva(String userAva) {
        this.userAva = userAva;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRegisterTime() {
        return userRegisterTime;
    }

    public void setUserRegisterTime(String userRegisterTime) {
        this.userRegisterTime = userRegisterTime;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserCarrer() {
        return userCarrer;
    }

    public void setUserCarrer(String userCarrer) {
        this.userCarrer = userCarrer;
    }

    public String getUserHobby() {
        return userHobby;
    }

    public void setUserHobby(String userHobby) {
        this.userHobby = userHobby;
    }

    public String getUserLangeage() {
        return userLangeage;
    }

    public void setUserLangeage(String userLangeage) {
        this.userLangeage = userLangeage;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public boolean isUserPhoneValidated() {
        return userPhoneValidated;
    }

    public void setUserPhoneValidated(boolean userPhoneValidated) {
        this.userPhoneValidated = userPhoneValidated;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isUserEmailValidated() {
        return userEmailValidated;
    }

    public void setUserEmailValidated(boolean userEmailValidated) {
        this.userEmailValidated = userEmailValidated;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }

    public boolean isUserIdentityValidated() {
        return userIdentityValidated;
    }

    public void setUserIdentityValidated(boolean userIdentityValidated) {
        this.userIdentityValidated = userIdentityValidated;
    }

//    public static OrderPersonRenderData getInstance(UserInfo userInfo) {
//        return new OrderPersonRenderData(userInfo.getHeadPic(),userInfo.getNick(),userInfo.getGmtCreated(),
//                userInfo.getRealName(),"none in userinfo",userInfo.getGender(), userInfo.getCountry()+" "+userInfo.getCity(),
//                userInfo.getCareer(),userInfo.getInterests(),userInfo.getLanguage(),userInfo.getSign(),
//                ) ;
//    }

    public static OrderPersonRenderData mock( ){
        return new OrderPersonRenderData("header","nick","1991-2-3","real","birth","","country city","carrer"
                ,"hobby","language","sign","",true,"",false,"identity",true);
    }
}
