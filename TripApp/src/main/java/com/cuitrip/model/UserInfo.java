package com.cuitrip.model;

import java.io.Serializable;

public class UserInfo implements Serializable{
    private static final long serialVersionUID = 4899849840118146981L;

    public static final int USER_TRAVEL = 0;
    public static final int USER_FINDER = 1;
//    "uid": "3", 12345678912 123456
//            "nick": "Rosa",
//            "status": "0",
//            "realName": "",
//            "country": "TW",
//            "city": "台灣",
//            "countryCode": "886",
//            "mobile": "0939081318",
//            "email": "",
//            "gender": "2",
//            "headPic": "http://cuitrip.oss-cn-shenzhen.aliyuncs.com/3_1434898588997",
//            "sign": "啟發他人，感動自己",
//            "language": "英文、中文",
//            "career": "Tripper!",
//            "interests": "調酒、美食、新奇事物",
//            "extInfo": "",
//            "gmtModified": "2015-07-08 10:36:31",
//            "gmtCreated": "2015-06-21 04:39:09",
//            "token": "d687f2911096a74316f521491c928ec0"

    private String uid; //": "3",
    private String nick; //: "Rosa",
    private String status; //": "0",
    private String realName; //": "",
    private String country; //: "TW",
    private String city; //: "台灣",
    private String countryCode; //: "886",
    private String mobile; //: "0939081318",
    private String email; //: "",
    private String gender; //: "2",
    private String headPic; //: "http://cuitrip.oss-cn-shenzhen.aliyuncs.com/3_1434898588997",
    private String sign; //: "啟發他人，感動自己",
    private String language; //: "英文、中文",
    private String career; //: "Tripper!",
    private String interests; //: "調酒、美食、新奇事物",
    private String extInfo; //: "",
    private String gmtModified; //: "2015-07-08 10:36:31",
    private String gmtCreated; //: "2015-06-21 04:39:09",
    private String token; //: "d687f2911096a74316f521491c928ec0"

    private int type; //用户身份 0：旅行者 1：发现者

    public boolean isTravel(){
        return type == USER_TRAVEL;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(String gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
