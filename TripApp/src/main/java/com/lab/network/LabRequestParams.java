package com.lab.network;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.business.BusinessHelper;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.lab.utils.LogHelper;
import com.loopj.android.http.RequestParams;

public class LabRequestParams extends RequestParams {
    public LabRequestParams() {
        super();
        setUseJsonStreamer(true);
        UserInfo info = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
        if (info != null) {
            put("uid", info.getUid());
            put("token", info.getToken());
            LogHelper.e("cancel order", "" + info.getUid() + "|" + info.getToken());
        } else {

        }
        put("lang", UnitUtils.getLanguage());
        put("moneyType", UnitUtils.getMoneyType());
        put("clientVersion", MainApplication.getInstance().getVersionName());

        put("version", BusinessHelper.API_VERSION);
        put("platform", "android");
    }

    public void setToken(Context context) {
    }

    public static JSONObject getJsonObject() {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        UserInfo info = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
        if (info != null) {
            jsonObject.put("uid", info.getUid());
            jsonObject.put("token", info.getToken());
            LogHelper.e("cancel order", "" + info.getUid() + "|" + info.getToken());
        } else {

        }
        jsonObject.put("lang", UnitUtils.getLanguage());
        jsonObject.put("moneyType", UnitUtils.getMoneyType());
        jsonObject.put("clientVersion", MainApplication.getInstance().getVersionName());

        jsonObject.put("version", BusinessHelper.API_VERSION);
        jsonObject.put("platform", "android");
        return jsonObject;
    }
}
