package com.cuitrip.business;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.cuitrip.app.MainApplication;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.lab.location.CtLocation;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabRequestParams;
import com.lab.utils.LogHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;

import org.apache.http.entity.ByteArrayEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class ServiceBusiness {
    public static RequestHandle commitServiceInfo(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                  String sid, String name, String address,
                                                  String desc, List<String> pic, String backPic, String price,
                                                  String maxbuyerNum, String serviceTime, String bestTime,
                                                  String meetingWay, int priceType, String country, String moneyType,
                                                  CtLocation location,
                                                  String meetingPlace,
                                                  String serviceTags,
                                                  String inlclude,
                                                  String exclude,
                                                  String lat,
                                                  String lng) {
        //LabRequestParams params = new LabRequestParams();
        JSONObject object = new JSONObject();
        //params.setToken(context);
        UserInfo info = LoginInstance.getInstance(MainApplication.sContext).getUserInfo();
        if (info != null) {
            object.put("uid", info.getUid());
            object.put("token", info.getToken());
        }
        if(!TextUtils.isEmpty(sid)){
            object.put("sid", sid);
        }
        object.put("name", name);
        object.put("address", address);
        object.put("descpt", desc);
        object.put("backPic", backPic);
        object.put("price", price);
        object.put("maxbuyerNum", maxbuyerNum);
        object.put("serviceTime", serviceTime);
//        object.put("bestTime", "10");
//        object.put("meetingWay", "0");
        object.put("priceType", priceType); //计费方式：0 一口价;1 按人计费;2 免费
        object.put("city", address);
        object.put("lat", location.latitude);
        object.put("lng", location.longitude);
        object.put("country", country);
        object.put("moneyType", moneyType);

        StringBuilder sb1 = new StringBuilder(object.toJSONString());
        sb1.delete(sb1.lastIndexOf("}"), sb1.length());
        sb1.append(",\"pic\":[");
        for (int i = 0; i < pic.size(); i++) {
            if (i > 0) {
                sb1.append(",");
            }
            sb1.append("\"").append(pic.get(i)).append("\"");
        }
        sb1.append("]}");
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(sb1.toString().getBytes("utf-8"));
            entity.setChunked(true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogHelper.e("omg","send commitServiceInfo"+object.toJSONString());
        return client.post(context, BusinessHelper.getApiUrl("commitServiceInfo"), entity, "application/json", handler);
    }

    public static RequestHandle modifyServiceInfo(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                  String sid, List<Long> availableDate) {
//        LabRequestParams params = new LabRequestParams();
//        params.setToken(context);
        JSONObject object = new JSONObject();
        //params.setToken(context);
        UserInfo info = LoginInstance.getInstance(MainApplication.sContext).getUserInfo();
        if (info != null) {
            object.put("uid", info.getUid());
            object.put("token", info.getToken());
        }
        object.put("sid", sid);
        //object.put("availableDate", availableDate);

        StringBuilder sb1 = new StringBuilder(object.toJSONString());
        sb1.delete(sb1.lastIndexOf("}"), sb1.length());
        sb1.append(",\"availableDate\":[");
        for (int i = 0; i < availableDate.size(); i++) {
            if (i > 0) {
                sb1.append(",");
            }
            sb1.append("\"").append(availableDate.get(i)).append("\"");
        }
        sb1.append("]}");
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(sb1.toString().getBytes("utf-8"));
            entity.setChunked(true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return client.post(context, BusinessHelper.getApiUrl("modifyServiceInfo"), entity, "application/json", handler);
    }

    /**
     * 发现者获取服务列表
     */
    public static RequestHandle getServiceList(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        return client.post(context, BusinessHelper.getApiUrl("getServiceList"), params, handler);
    }

    public static RequestHandle getServiceAvailableDate(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                        String sid) {
        LabRequestParams params = new LabRequestParams();
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("getServiceAvailableDate"), params, handler);
    }

    public static RequestHandle getServiceAvailableAndBookedDate(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                        String sid) {
        LabRequestParams params = new LabRequestParams();
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("getServiceBookedAndAvailableDate"), params, handler);
    }

    public static RequestHandle deleteServiceInfo(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                  String sid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("deleteServiceInfo"), params, handler);
    }

    public static RequestHandle getRecommendList(Context context, AsyncHttpClient clinet, LabAsyncHttpResponseHandler handler,
                                                 String country, String uid, int start, int size) {
        LabRequestParams params = new LabRequestParams();
        params.put("country", country);
        if (LoginInstance.getInstance(context).getUserInfo() != null
                && !TextUtils.isEmpty(LoginInstance.getInstance(context).getUserInfo().getUid())) {
            params.put("uid", LoginInstance.getInstance(context).getUserInfo().getUid());
        }
        params.put("start", start);
        params.put("size", size);
        return clinet.post(context, BusinessHelper.getApiUrl("getRecommendList"), params, handler);
    }

    public static RequestHandle getServiceDetail(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                 String sid) {
        LabRequestParams params = new LabRequestParams();
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("getServiceDetail"), params, handler);
    }


    public static RequestHandle delPic(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                       String picUrl) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("picUrl", picUrl);
        return client.post(context, BusinessHelper.getApiUrl("delPic"), params, handler);
    }

    public static RequestHandle upPic(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                      String picBase64) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("picBase64", picBase64);
        return client.post(context, BusinessHelper.getApiUrl("upPic"), params, handler);
    }


    public static RequestHandle getServiceTags(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                               String language) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("lang", language);
        return client.post(context, BusinessHelper.getApiUrl("getServiceTags"), params, handler);
    }

    public static RequestHandle getCountryCity(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler
                                              ,String language, String contry) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("lang", language);
        params.put("contry", contry);
        return client.post(context, BusinessHelper.getApiUrl("getCountryCity"), params, handler);
    }

    public static RequestHandle getStatistic(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                             String sid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("getStatistic"), params, handler);
    }

    public static RequestHandle revertLikeService(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                  String sid) {
        LabRequestParams params = new LabRequestParams();
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("addLikes"), params, handler);
    }

    public static RequestHandle getLikes(Context context, AsyncHttpClient clinet, LabAsyncHttpResponseHandler handler,
                                                  int start, int size) {
        LabRequestParams params = new LabRequestParams();
        if (LoginInstance.getInstance(context).getUserInfo() != null
                && !TextUtils.isEmpty(LoginInstance.getInstance(context).getUserInfo().getUid())) {
            params.put("uid", LoginInstance.getInstance(context).getUserInfo().getUid());
        }
        params.put("start", start);
        params.put("size", size);
        return clinet.post(context, BusinessHelper.getApiUrl("getLikes"), params, handler);
    }
}
