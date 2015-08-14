package com.cuitrip.model;


import com.alibaba.fastjson.JSON;
import com.lab.utils.Constants;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceInfo implements Serializable, ServiceListInterface {

    private static final long serialVersionUID = -3984338984164873535L;
    public static final int STATUS_CHECKING = 0;
    public static final int STATUS_CHECHED = 1;
    public static final int STATUS_CHECK_FAILED = 2;

    public boolean isChecking() {
        return STATUS_CHECKING == checkStatus;
    }

    public boolean isChecked() {
        return STATUS_CHECHED == checkStatus;
    }

    public boolean isCheckFailed() {
        return STATUS_CHECK_FAILED == checkStatus;
    }

    public static final int PAYWAY_ALL = 0;
    public static final int PAYWAY_PER = 1;
    public static final int PAYWAY_FREE = 2;
//    public static final String[] PAY_TYPE = new String[]{"一口价", "按人计费", "免费"};

    //    "sid": 0,
//            "name": "阿亮带你看妈祖绕境",
//            "status": 0,
//            "checkStatus": 0, //审核状态：0. 审核中；1. 已审核；2. 审核未通过
//            "type": 0,
//            "address": "彰化县",
//            "lat": null,
//            "lng": null,
//            "proc": null,
//            "city": null,
//            "insiderId": 1,
//            "score": 0,
//            "descpt": "阿亮带你看妈祖绕境",
//            "pic": "[\"http://alicdn.aliyun.com/pic2.jpg\",\"http://alicdn.aliyun.com/pic2.jpg\"]",
//            "backPic": null,
//            "tag": null,
//            "moneyType": null,
//            "price": 300,
//            "priceType": //计费方式：0 一口价;1 按人计费;2 免费
//            "priceDesc": null,
//            "maxbuyerNum": 5,
//            "serviceTime": "3h",
//            "bestTime": "9:00~11:00",
//            "availableDate": 0,
//            "meetingWay": 2,
//            "extInfo": null,
//            "isDeleted": 0,
//            "gmtCreated": null,
//            "gmtModified": null,
//            "country": null
    private String sid; //": 0,
    private String name; //": "阿亮带你看妈祖绕境",
    private String status; //": 0,
    private int checkStatus; //": 0,
    private String type; //": 0,
    private String address; //": "彰化县",
    private String lat; //": null,
    private String lng; //": null,
    private String proc; //": null,
    private String city; //": null,
    private String insiderId; //": 1,
    private String score; //": 0,
    private String descpt; //": "阿亮带你看妈祖绕境",
    private List<String> pic; //": "[\"http://alicdn.aliyun.com/pic2.jpg\",\"http://alicdn.aliyun.com/pic2.jpg\"]",
    private String backPic; //": null,
    private String tag; //": null,
    private String moneyType; //": null,
    private String price; //": 300,
    private String priceDesc; //": null,
    private Integer maxbuyerNum; //": 5,
    private String serviceTime; //": "3h",
    private String bestTime; //": "9:00~11:00",
    private String availableDate; //": 0,
    private String meetingWay; //": 2,
    private String extInfo; //": null,
    private String isDeleted; //": 0,
    private String gmtCreated; //": null,
    private String gmtModified; //": null,
    private String country; //": null
    private Integer priceType;

    private String descptWithNoPic;

    private String tags;
    private String meetLocation;
    private String priceInclude;
    private String priceUninclude;

    // tag &&tags??
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public Integer getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getProc() {
        return proc;
    }

    public void setProc(String proc) {
        this.proc = proc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInsiderId() {
        return insiderId;
    }

    public void setInsiderId(String insiderId) {
        this.insiderId = insiderId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDescpt() {
        return descpt;
    }

    public String getDescptWithnoPic() {
        return descptWithNoPic;
    }

    public void setDescpt(String descpt) {
        this.descpt = descpt;
        try {
            if (descpt != null) {
                StringBuilder sb = new StringBuilder();
                Pattern pattern = Pattern.compile(Constants.IMAGE_PATTERN);
                Matcher matcher = pattern.matcher(descpt);
                int index = 0;
                while (matcher.find()) {
                    int start = matcher.start();
                    String temp = descpt.substring(index, start);
                    index = matcher.end();
                    sb.append(temp);
                }
                if (index < descpt.length() - 1) {
                    sb.append(descpt.substring(index, descpt.length()));
                }
                descptWithNoPic = sb.toString();
            }
        } catch (Exception e) {
            descptWithNoPic = descpt;
        }
    }

    public List<String> getPic() {
        return pic;
    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }

    public String getBackPic() {
        return backPic;
    }

    public void setBackPic(String backPic) {
        this.backPic = backPic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        this.priceDesc = priceDesc;
    }

    public Integer getMaxbuyerNum() {
        return maxbuyerNum;
    }

    public void setMaxbuyerNum(int maxbuyerNum) {
        this.maxbuyerNum = maxbuyerNum;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getBestTime() {
        return bestTime;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getMeetingWay() {
        return meetingWay;
    }

    public void setMeetingWay(String meetingWay) {
        this.meetingWay = meetingWay;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public ExtInfo getExtInfoObject() {
        if (extInfo == null) {
            return null;
        }
        try {
            return JSON.parseObject(extInfo, ExtInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(String gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static class ExtInfo {
        private String refuseReason;

        public String getRefuseReason() {
            return refuseReason;
        }

        public void setRefuseReason(String refuseReason) {
            this.refuseReason = refuseReason;
        }
    }
}
