package com.cuitrip.model;

import java.util.List;

/**
 * Created by baziii on 15/8/24.
 */
public class Bills {

    public String  moneyType;
    public String  balance;
    public String  rate;
    private List<BillData> lists;

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public List<BillData> getLists() {
        return lists;
    }

    public void setLists(List<BillData> lists) {
        this.lists = lists;
    }
}
