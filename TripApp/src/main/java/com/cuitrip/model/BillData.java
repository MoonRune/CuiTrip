package com.cuitrip.model;

/**
 * Created by baziii on 15/8/18.
 */
public class BillData {

//    "title": "提现", 			// 账目名称
//            "moneyType": "USD", 		// 取现货币，这个根据数据库记录来显示，其他的按入参的moneyType 显示
//            "money": "100",			// 取现金额
//            "headPic": "********",		// 头像url
//            "gmtModified": "2015-08-10 11:10:10"

    private String title;
    private String moneyType;
    private String money;
    private String headPic;
    private String gmtModified;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }
}
