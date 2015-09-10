package com.cuitrip.app.conversation.rong;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.ListFetchCallback;
import com.cuitrip.app.conversation.ConversationItem;
import com.cuitrip.app.conversation.IConversationsFetcher;
import com.cuitrip.app.rong.RongCloudEvent;
import com.cuitrip.app.rong.RongTitleTagHelper;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by baziii on 15/8/7.
 * <p/>
 * <p/>
 * wtf
 * 1.fetch conversation list (attention : rongim conversation list return empty name);
 * 2.fetch conversation names and inject ; get oid finerid travelid
 * 3.filter avaliable conversations
 * 4.fetch avaliable conversations' user name &&ava &&service name
 * 5.build display item;
 */
public class ConversationFetcherRong implements IConversationsFetcher {
    public static final String TAG = "ConversationFetcherRong";

    AsyncHttpClient mClient = new AsyncHttpClient();

    public static final int DEFAULT_SIZE = 10;

    @Override
    public void getConversations(final ListFetchCallback<ConversationItem> itemListFetchCallback) {
        final UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();

        OrderBusiness.getOrderList(MainApplication.getInstance(), mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                try {
                    final List<OrderItem> mOrderDatas = JSON.parseArray(data.toString(), OrderItem.class);
                    if (mOrderDatas != null && !mOrderDatas.isEmpty()) {
                        LogHelper.e(TAG, "order size" + mOrderDatas.size());
                        requestRong(itemListFetchCallback, userInfo, mOrderDatas);
                        LogHelper.e(TAG, "order size excuted");
                    } else {
                        itemListFetchCallback.onSuc(new ArrayList<ConversationItem>());
                    }
                } catch (Exception e) {
                    itemListFetchCallback.onFailed(new CtException(PlatformUtil.getInstance().getString(R.string.data_error)));
                }
            }


            @Override
            public void onFailure(LabResponse response, Object data) {

            }
        }, userInfo.isTravel() ? 1 : 2, 0, DEFAULT_SIZE);

    }

    @Override
    public void getConversationsMore(final ListFetchCallback<ConversationItem> itemListFetchCallback, int from) {
        final UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();

        OrderBusiness.getOrderList(MainApplication.getInstance(), mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                try {
                    final List<OrderItem> mOrderDatas = JSON.parseArray(data.toString(), OrderItem.class);
                    if (mOrderDatas != null && !mOrderDatas.isEmpty()) {
                        LogHelper.e(TAG, "order size" + mOrderDatas.size());
                        requestRong(itemListFetchCallback, userInfo, mOrderDatas);
                        LogHelper.e(TAG, "order size excuted");
                    } else {
                        itemListFetchCallback.onSuc(new ArrayList<ConversationItem>());
                    }
                } catch (Exception e) {
                    itemListFetchCallback.onFailed(new CtException(PlatformUtil.getInstance().getString(R.string.data_error)));
                }
            }


            @Override
            public void onFailure(LabResponse response, Object data) {

            }
        }, userInfo.isTravel() ? 1 : 2, from, from + DEFAULT_SIZE);

    }

    public void filterName(Conversation conversation, ConversationItem item) {
        item.setId(conversation.getTargetId());
        item.setUnreadCount(conversation.getUnreadMessageCount());
        item.setTime(String.valueOf(RongTitleTagHelper.buildDateString(conversation.getSentTime())));
        item.setLast(conversation.getSentTime());

        item.setLastWords(RongCloudEvent.getMessageContent(conversation.getLatestMessage()));
    }

    public void requestRong(final ListFetchCallback<ConversationItem> itemListFetchCallback, final UserInfo userInfo, final List<OrderItem> mOrderDatas) {
        new AsyncTask() {
            final List<ConversationItem> sorteds = new ArrayList();

            @Override
            protected Object doInBackground(Object[] params) {
                final HashMap<String, ConversationItem> result = new HashMap<String, ConversationItem>();
                final ArrayList<ConversationItem> lefted = new ArrayList<ConversationItem>();
                for (OrderItem orderItem : mOrderDatas) {
                    if (!TextUtils.isEmpty(orderItem.getTargetId())) {
                        result.put(orderItem.getTargetId(), new ConversationItem(
                                "",
                                orderItem.getUserNick(),
                                0,
                                orderItem.getServiceName(),
                                "",
                                "",
                                orderItem.getHeadPic(), orderItem.getOid()));
                        LogHelper.e(TAG, "put  " + orderItem.getOid());
                    } else {
                        lefted.add(new ConversationItem(
                                "",
                                orderItem.getUserNick(),
                                0,
                                orderItem.getServiceName(),
                                "",
                                "",
                                orderItem.getHeadPic(), orderItem.getOid()));
                        LogHelper.e(TAG, "put  " + orderItem.getOid());

                    }
                }
                LogHelper.e(TAG, "result size" + result.size());


                try {
                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                    if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
                        RongIM.getInstance().getRongIMClient().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                            @Override
                            public void onSuccess(final List<Conversation> conversations) {
                                if (conversations == null) {
                                    LogHelper.e(TAG, "empty conversation");
                                } else {
                                    for (Conversation conversation : conversations) {
                                        if (result.containsKey(conversation.getTargetId())) {
                                            LogHelper.e(TAG, "has " + conversation.getTargetId());
                                            ConversationItem item = result.get(conversation.getTargetId());
                                            filterName(conversation, item);
                                        }
                                    }
                                }
                                LogHelper.e(TAG, " ok ");
                                sorteds.addAll(result.values());
                                sorteds.addAll(lefted);
                                countDownLatch.countDown();
                            }

                            public void onError(RongIMClient.ErrorCode errorCode) {
                                LogHelper.e("fetch conversations", "|error|" + errorCode);

                                countDownLatch.countDown();
                            }

                        }, Conversation.ConversationType.DISCUSSION);
                    }
                    countDownLatch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sorteds != null) {
                    Collections.sort(sorteds, new Comparator<ConversationItem>() {
                        @Override
                        public int compare(ConversationItem lhs, ConversationItem rhs) {
                            //long cast
                            return (rhs.getLast() - lhs.getLast()) > 0 ? 3 : -3;
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                LogHelper.e(TAG, "sorteds size" + sorteds.size());
                for (ConversationItem orderItem : sorteds) {
                    LogHelper.e(TAG, " result:" + orderItem.toString());
                }
                itemListFetchCallback.onSuc(sorteds);
//                            itemListFetchCallback.onFailed(new CtException(PlatformUtil.getInstance().getString(R.string.data_error)));
                super.onPostExecute(o);
            }

        }.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static String getName(UserInfo userInfo) {
        if (userInfo == null) {
            return "";
        }
        return userInfo.getNick();
    }

}
