package com.cuitrip.business;

import android.content.Context;
import android.text.TextUtils;

import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabRequestParams;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;

public class UserBusiness {

    public static RequestHandle login(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                         String mobile, String countryCode, String passwd) {
        LabRequestParams params = new LabRequestParams();
        params.put("mobile", mobile);
        params.put("countryCode", countryCode);
        params.put("passwd", passwd);
        return client.post(context, BusinessHelper.getApiUrl("login"), params, handler);
    }

    public static RequestHandle register(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                      String mobile, String countryCode, String passwd, String vcode, String nick) {
        LabRequestParams params = new LabRequestParams();
        params.put("mobile", mobile);
        params.put("countryCode", countryCode);
        params.put("newPasswd", passwd);
        params.put("vcode", vcode);
        params.put("nick", nick);
        params.put("client", "android");
        return client.post(context, BusinessHelper.getApiUrl("register"), params, handler);
    }

    public static RequestHandle logout(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler
                                      ) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        return client.post(context, BusinessHelper.getApiUrl("logout"), params, handler);
    }

    public static RequestHandle checkVcode(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                      String mobile, String countryCode, String vcode) {
        LabRequestParams params = new LabRequestParams();
        params.put("mobile", mobile);
        params.put("countryCode", countryCode);
        params.put("vcode", vcode);
        return client.post(context, BusinessHelper.getApiUrl("checkVcode"), params, handler);
    }

    public static RequestHandle modifyPassword(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                       String newPasswd, String rePasswd) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("newPasswd", newPasswd);
        params.put("rePasswd", rePasswd);
        return client.post(context, BusinessHelper.getApiUrl("modifyPassword"), params, handler);
    }
    public static RequestHandle resetPassword(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                         String mobile, String countryCode, String newPasswd, String vcode, String rePasswd) {
        LabRequestParams params = new LabRequestParams();
        params.put("mobile", mobile);
        params.put("countryCode", countryCode);
        params.put("newPasswd", newPasswd);
        params.put("vcode", vcode);
        params.put("rePasswd", rePasswd);
        params.put("client", "android");
        return client.post(context, BusinessHelper.getApiUrl("resetPassword"), params, handler);
    }

    public static RequestHandle modifyUserInfo(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                         String realName, String nick,
                                               String gender, String city, String language, String career,
                                               String interests, String sign) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("realName", realName);
        params.put("nick", nick);
        params.put("gender", gender);
        params.put("city", city);
        params.put("language", language);
        params.put("career", career);
        params.put("interests", interests);
        params.put("sign", sign);

        return client.post(context, BusinessHelper.getApiUrl("modifyUserInfo"), params, handler);
    }

    public static RequestHandle upDevicetoken(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                              String deviceToken) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("client", "android");
        params.put("deviceToken", deviceToken);
        return client.post(context, BusinessHelper.getApiUrl("upDevicetoken"), params, handler);
    }

    public static RequestHandle changeType(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                              int type) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("type", type);
        return client.post(context, BusinessHelper.getApiUrl("changeType"), params, handler);
    }

    public static RequestHandle updateIntroduce(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                  String uid, String token, String content) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("token", token);
        params.put("uid", uid);
        params.put("content", content);
        return client.post(context, BusinessHelper.getApiUrl("updateIntroduce"), params, handler);
    }

    public static RequestHandle getIntroduce(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        return client.post(context, BusinessHelper.getApiUrl("getIntroduce"), params, handler);
    }



    public static RequestHandle updateProfile(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                              String headPic,
                                              String realName, String nick, String gender, String city,
                                              String language, String career, String interests, String sign) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        if (!TextUtils.isEmpty(realName)) {
            params.put("realName", realName);
        }
        if (!TextUtils.isEmpty(realName)) {
            params.put("gender", gender);
        }
        params.put("nick", nick);
        params.put("city", city);
        params.put("language", language );
        params.put("career", career);
        params.put("interests", interests);
        params.put("sign", sign);
        params.put("headPic", headPic);
        return client.post(context, BusinessHelper.getApiUrl("modifyUserInfo"), params, handler);
    }
}
