package com.cuitrip.app.orderdetail;

import com.cuitrip.app.pro.CommentPartRenderData;
import com.cuitrip.app.pro.OrderProgressingRenderData;
import com.cuitrip.app.pro.ServicePartRenderData;

/**
 * Created by baziii on 15/8/11.
 */
public class OrderMode {
    private ServicePartRenderData mServiceData;
    private CommentPartRenderData mCommentData;
    private OrderProgressingRenderData mProgressData;
    private String mMenuText; // NULL GONE
    private String mBottomText;// NULL GONE
    private boolean mBottomEnable;

    public OrderMode(ServicePartRenderData mServiceData, CommentPartRenderData mCommentData, OrderProgressingRenderData mProgressData, String mMenuText, String mBottomText, boolean mBottomEnable) {
        this.mServiceData = mServiceData;
        this.mCommentData = mCommentData;
        this.mProgressData = mProgressData;
        this.mMenuText = mMenuText;
        this.mBottomText = mBottomText;
        this.mBottomEnable = mBottomEnable;
    }

    public ServicePartRenderData getServiceData() {
        return mServiceData;
    }

    public void setServiceData(ServicePartRenderData mServiceData) {
        this.mServiceData = mServiceData;
    }

    public CommentPartRenderData getCommentData() {
        return mCommentData;
    }

    public void setCommentData(CommentPartRenderData mCommentData) {
        this.mCommentData = mCommentData;
    }

    public OrderProgressingRenderData getProgressData() {
        return mProgressData;
    }

    public void setProgressData(OrderProgressingRenderData mProgressData) {
        this.mProgressData = mProgressData;
    }

    public String getMenuText() {
        return mMenuText;
    }

    public void setMenuText(String mMenuText) {
        this.mMenuText = mMenuText;
    }

    public String getBottomText() {
        return mBottomText;
    }

    public void setBottomText(String mBottomText) {
        this.mBottomText = mBottomText;
    }

    public boolean isBottomEnable() {
        return mBottomEnable;
    }

    public void setBottomEnable(boolean mBottomEnable) {
        this.mBottomEnable = mBottomEnable;
    }
}
