package com.cuitrip.model;

import java.util.List;

public class ReviewList {
    private int num;
    private List<ReviewListItem> list;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<ReviewListItem> getList() {
        return list;
    }

    public void setList(List<ReviewListItem> list) {
        this.list = list;
    }
}
