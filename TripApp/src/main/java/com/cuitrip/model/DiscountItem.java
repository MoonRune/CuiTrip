package com.cuitrip.model;

/**
 * Created by baziii on 15/8/19.
 */
public class DiscountItem {
//    "code":"1",				// 券id
//            "money":"500",				// 优惠金额
//            "moneyType":"CNY",			// 币种
//            "validDate":"2015-09-07 00:00:00"	// 有效期
    private String code;
    private String money;
    private String moneyType;
    private String invalidDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(String invalidDate) {
        this.invalidDate = invalidDate;
    }
}
