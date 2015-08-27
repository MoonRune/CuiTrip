package com.cuitrip.business;

import android.content.Context;

import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabRequestParams;
import com.lab.utils.LogHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;


public class MessageBusiness {
    public static RequestHandle getMessageList(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                               int userType,int start,int size) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("userType", userType);
        params.put("version", "1.0");
        params.put("start", start);
        params.put("size", size);
        LogHelper.e("getMessageList",params.toString());
        return client.post(context, BusinessHelper.getApiUrl("getMessageList"), params, handler);
    }
    public static RequestHandle deleteMessage(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                              String messageId) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("messageId", messageId);
        LogHelper.e("deleteMessage",params.toString());
        return client.post(context, BusinessHelper.getApiUrl("deleteMessage"), params, handler);
    }


    public static RequestHandle getDialogList(Context context, AsyncHttpClient client,
                                              LabAsyncHttpResponseHandler handler, String orderId,
                                              int size, int start) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("orderId", orderId);
        params.put("size", size);
        params.put("start", start);
        return client.post(context, BusinessHelper.getApiUrl("getDialogList"), params, handler);
    }
    public static RequestHandle putDialog(Context context, AsyncHttpClient client,
                                              LabAsyncHttpResponseHandler handler, String orderId,
                                              String token, String send, String receiver, String content, String sid) {
        LabRequestParams params = new LabRequestParams();
        //params.setToken(context);
        params.put("token", token);
        params.put("orderId", orderId);
        params.put("send", send);
        params.put("receiver", receiver);
        params.put("content", content);
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("putDialog"), params, handler);
    }
}
