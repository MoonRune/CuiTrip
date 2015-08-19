package com.cuitrip.app.conversation.rong;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.ListFetchCallback;
import com.cuitrip.app.conversation.ConversationItem;
import com.cuitrip.app.conversation.IConversationsFetcher;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.DiscussionNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

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

    @Override
    public void getConversations(final ListFetchCallback<ConversationItem> itemListFetchCallback) {
        final UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();

        OrderBusiness.getOrderList(MainApplication.getInstance(), mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                try {
                    final String myId = userInfo.getUid();
                    final List<OrderItem> mOrderDatas = JSON.parseArray(data.toString(), OrderItem.class);
                    new AsyncTask() {
                        public String getOtherName(String id, String id2, String name1, String name2) {
                            if (myId.equals(id)) {
                                return name1;
                            }
                            return name2;
                        }

                        final List<ConversationItem> sorteds= new ArrayList();
                        @Override
                        protected Object doInBackground(Object[] params) {
                            final HashMap<String, ConversationItem> result = new HashMap<String, ConversationItem>();
                            for (OrderItem orderItem : mOrderDatas) {
                                result.put(orderItem.getTargetId(), new ConversationItem(
                                        "",
                                        getOtherName(orderItem.getInsiderId(), orderItem.getTravellerId(), orderItem.getInsiderName(), orderItem.getTravellerName()),
                                        0,
                                        orderItem.getServiceName(),
                                        "空",
                                        "",
                                        orderItem.getHeadPic(), orderItem.getOid()));
                            }
                            final CountDownLatch countDownLatch = new CountDownLatch(1);
                            RongIM.getInstance().getRongIMClient().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                                @Override
                                public void onSuccess(final List<Conversation> conversations) {
                                    if (conversations == null) {
                                        LogHelper.e(TAG, "empty conversation" );
                                        return;
                                    }
                                    for (Conversation conversation : conversations) {
                                        if (result.containsKey(conversation.getTargetId())) {
                                            LogHelper.e(TAG, "has "+conversation.getTargetId() );
                                            ConversationItem item = result.get(conversation.getTargetId());
                                            filterName(conversation, item);
                                            sorteds.add(item);
                                        }
                                    }
                                    LogHelper.e(TAG, " ok " );
                                    sorteds.addAll(result.values());
                                    countDownLatch.countDown();
                                }

                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    LogHelper.e("fetch conversations", "|error|" + errorCode);

                                    countDownLatch.countDown();
                                }

                            }, Conversation.ConversationType.DISCUSSION);
                            try {
                                countDownLatch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            itemListFetchCallback.onSuc(sorteds);
//                            itemListFetchCallback.onFailed(new CtException(PlatformUtil.getInstance().getString(R.string.data_error)));
                            super.onPostExecute(o);
                        }

                    }.execute(AsyncTask.THREAD_POOL_EXECUTOR);

                } catch (Exception e) {
                    itemListFetchCallback.onFailed(new CtException(PlatformUtil.getInstance().getString(R.string.data_error)));
                }
            }

            public void filterName(Conversation conversation, ConversationItem item) {
                item.setId(conversation.getTargetId());
                item.setUnreadCount(conversation.getUnreadMessageCount());
                item.setTime(String.valueOf(RongTitleTagHelper.buildDateString(conversation.getSentTime())));
                if (conversation.getLatestMessage() == null) {
                    item.setLastWords("");

                } else if (conversation.getLatestMessage() instanceof VoiceMessage) {
                    VoiceMessage voiceMessage = (VoiceMessage) conversation.getLatestMessage();
                    item.setLastWords("[语音]");
                } else if (conversation.getLatestMessage() instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) conversation.getLatestMessage();
                    item.setLastWords(textMessage.getContent());

                } else if (conversation.getLatestMessage() instanceof RichContentMessage) {
                    RichContentMessage richContentMessage = (RichContentMessage) conversation.getLatestMessage();
                    item.setLastWords(richContentMessage.getContent());
                } else if (conversation.getLatestMessage() instanceof DiscussionNotificationMessage) {
                    DiscussionNotificationMessage discussionNotificationMessage = (DiscussionNotificationMessage) conversation.getLatestMessage();

                    item.setLastWords("[提醒]");
                } else if (conversation.getLatestMessage() instanceof ImageMessage) {
                    item.setLastWords("[图片]");
                } else {
                    item.setLastWords("其他消息");
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {

            }
        }, userInfo.isTravel() ? 1 : 2);

    }

    public static String getName(UserInfo userInfo) {
        if (userInfo == null) {
            return "";
        }
        return userInfo.getNick();
    }

    public static String getAva(UserInfo userInfo) {
        if (userInfo == null) {
            return "";
        }
        return userInfo.getHeadPic();
    }

    public static String geServicetName(OrderItem userInfo) {
        if (userInfo == null) {
            return "";
        }
        return userInfo.getServiceName();
    }
}
