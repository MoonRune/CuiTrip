package com.lab.network;

import android.content.Context;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.lab.utils.LogHelper;
import com.loopj.android.http.RequestParams;

public class LabRequestParams extends RequestParams {
    public LabRequestParams() {
        super();
        setUseJsonStreamer(true);
    }

    public void setToken(Context context) {
        UserInfo info = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
        if (info != null) {
            put("uid", info.getUid());
            put("token", info.getToken());
            LogHelper.e("cancel order", "" + info.getUid() + "|" + info.getToken());
        } else {

        }
        put("lang", UnitUtils.getLanguage());
        put("moneyType", UnitUtils.getMoneyType());
        put("version", MainApplication.getInstance().getVersionName());
        put("platform", "android");
    }
}
