package com.cuitrip.business;

import android.content.Context;

import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabRequestParams;
import com.lab.utils.LogHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;


public class OrderBusiness {
    public static RequestHandle getOrderList(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                             int userType,int start, int end) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("userType", userType);
        params.put("start", start);
        params.put("size", end);
        return client.post(context, BusinessHelper.getApiUrl("getOrderList"), params, handler);
    }

    public static RequestHandle getOrderInfo(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                             String oid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("oid", oid);
        return client.post(context, BusinessHelper.getApiUrl("getOrderInfo"), params, handler);
    }

    /**
     * 旅行者取消旅程
     *
     * @param context
     * @param client
     * @param handler
     * @param oid
     * @return
     */
    public static RequestHandle cancelOrder(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                            String oid, String reason) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("oid", oid);
        params.put("reason", reason);
        params.put("type", "1");
        return client.post(context, BusinessHelper.getApiUrl("cancelOrder"), params, handler);
    }


    public static RequestHandle refuseOrder(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                            String oid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("oid", oid);
        params.put("type", "2");
        return client.post(context, BusinessHelper.getApiUrl("cancelOrder"), params, handler);
    }

    /**
     * 发现者确认预约
     *
     * @param context
     * @param client
     * @param handler
     * @param oid
     * @return
     */
    public static RequestHandle confirmOrder(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                             String oid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("oid", oid);
        return client.post(context, BusinessHelper.getApiUrl("confirmOrder"), params, handler);
    }

    /**
     * 开始旅程
     *
     * @param context
     * @param client
     * @param handler
     * @param oid
     * @return
     */
    public static RequestHandle beginOrder(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                           String oid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("oid", oid);
        return client.post(context, BusinessHelper.getApiUrl("beginOrder"), params, handler);
    }

    /**
     * 结束旅程，订单关闭
     *
     * @param context
     * @param client
     * @param handler
     * @param oid
     * @return
     */
    public static RequestHandle endOrder(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                         String oid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("oid", oid);
        return client.post(context, BusinessHelper.getApiUrl("endOrder"), params, handler);
    }

    public static RequestHandle submitReview(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                             String oid, String serviceScore, String content) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("oid", oid);
        params.put("serviceScore", serviceScore);
        params.put("content", content);
        return client.post(context, BusinessHelper.getApiUrl("submitReview"), params, handler);
    }

    public static RequestHandle getReviewCount(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                               String sid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("getReviewCount"), params, handler);
    }

    public static RequestHandle getReviewList(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                              String sid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("sid", sid);
        return client.post(context, BusinessHelper.getApiUrl("getReviewList"), params, handler);
    }

    public static RequestHandle getReviewInfo(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                              String rid) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("rid", rid);
        return client.post(context, BusinessHelper.getApiUrl("getReviewInfo"), params, handler);
    }

    public static RequestHandle createOrder(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                            String insiderId, String sid, String serviceName,
                                            long serviceDate, String buyerNum, String servicePrice,
                                            String moneyType) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("insiderId", insiderId);
        params.put("sid", sid);
        params.put("serviceName", serviceName);
        params.put("serviceDate", serviceDate);
        params.put("buyerNum", buyerNum);
        params.put("servicePrice", servicePrice);
        params.put("moneyType", moneyType);
        return client.post(context, BusinessHelper.getApiUrl("createOrder"), params, handler);
    }

    public static RequestHandle payOrder(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                         String orderId, String inviteCode) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("orderId", orderId);
        params.put("inviteCode", inviteCode);
        return client.post(context, BusinessHelper.getApiUrl("payOrder"), params, handler);
    }

    public static RequestHandle modifyOrder(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                            String orderId, String sid, String serviceName,
                                            long serviceDate, String buyerNum, String servicePrice,
                                            String moneyType) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("orderId", orderId);
        params.put("sid", sid);
        params.put("serviceName", serviceName);
        params.put("serviceDate", serviceDate);
        params.put("buyerNum", buyerNum);
        params.put("servicePrice", servicePrice);
        params.put("payCurrency", moneyType);
        return client.post(context, BusinessHelper.getApiUrl("modifyOrder"), params, handler);
    }

    public static RequestHandle getCharge(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                          String oid, String channel, String clientIp, String payCurrency,
                                          String couponId) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("orderId", oid);
        params.put("channel", channel);
        params.put("clientIp", clientIp);
        params.put("payCurrency", payCurrency);
        params.put("couponIds", couponId);
        return client.post(context, BusinessHelper.getApiUrl("getCharge"), params, handler);
    }


    public static RequestHandle getFinalPrice(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                          String oid, String payCurrency,
                                          String couponId) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("orderId", oid);
        params.put("payCurrency", payCurrency);
        params.put("couponIds", couponId);
        return client.post(context, BusinessHelper.getApiUrl("getFinalPrice"), params, handler);
    }

    public static RequestHandle notifyPayStatus(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                String orderId, boolean isSuccess) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("orderId", orderId);
        params.put("isSuccess", isSuccess ? "true" : "false");
        return client.post(context, BusinessHelper.getApiUrl("notifyPayStatus"), params, handler);
    }


    public static RequestHandle getBills(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                         String moneyType, int start, int size) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("moneyType", moneyType);
        params.put("size", size);
        params.put("start", start);
        LogHelper.e("getBills",params.toString());
        return client.post(context, BusinessHelper.getApiUrl("getBills"), params, handler);
    }

    public static RequestHandle getValidCoupon(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                               String payCurrency) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("payCurrency", payCurrency);
        return client.post(context, BusinessHelper.getApiUrl("getValidCoupon"), params, handler);
    }

    public static RequestHandle updateOrderConversation(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                        String orderId,String targetId) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("orderId", orderId);
        params.put("targetId", targetId);
        return client.post(context, BusinessHelper.getApiUrl("updateTargetId"), params, handler);
    }

    public static RequestHandle getOrderByRongTargetId(Context context, AsyncHttpClient client, LabAsyncHttpResponseHandler handler,
                                                        String targetId) {
        LabRequestParams params = new LabRequestParams();
        params.setToken(context);
        params.put("targetId", targetId);
        return client.post(context, BusinessHelper.getApiUrl("getInfoByTargetId"), params, handler);
    }

}
