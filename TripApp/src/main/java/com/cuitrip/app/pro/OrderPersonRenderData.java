package com.cuitrip.app.pro;

import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.utils.LogHelper;
import com.lab.utils.Utils;

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
    protected String introduce;
    protected boolean userPhoneValidated;
    protected String userEmail;
    protected boolean userEmailValidated;
    protected String userIdentity;
    protected boolean userIdentityValidated;

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public OrderPersonRenderData(String userAva, String userName, String userRegisterTime, String userRealName, String userBirth, String userGender, String userCity, String userCarrer, String userHobby, String userLangeage, String userSign, String userPhone, boolean userPhoneValidated, String userEmail, boolean userEmailValidated, String userIdentity, boolean userIdentityValidated,String introduce) {
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
        this.introduce = introduce;
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

    public static OrderPersonRenderData getInstance(UserInfo userInfo) {
        LogHelper.e("omg","get instance "+userInfo.getGmtCreated());
        return new OrderPersonRenderData(userInfo.getHeadPic(),
                userInfo.getNick(),
                PlatformUtil.getInstance().getString(R.string.regist_time_with_string_below,Utils.getMsToD(userInfo.getGmtCreated())),
                userInfo.getRealName(),
                userInfo.getBirthDay(),
                Utils.getGender(userInfo.getGender()),
                userInfo.getCountry()+" "+userInfo.getCity(),
                userInfo.getCareer(),
                userInfo.getInterests(),
                userInfo.getLanguage(),userInfo.getSign(),
                "",
                userInfo.isPhoneValidated(),
                "",
                userInfo.isEmailValidated(),
                "",
                userInfo.isIdentityValidated(),
                userInfo.getIntroduce()
                ) ;
    }

}
