package com.cuitrip.app.conversation.rong;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.app.base.ListFetchCallback;
import com.cuitrip.app.conversation.ConversationItem;
import com.cuitrip.app.conversation.IConversationsFetcher;
import com.cuitrip.app.rong.RongTitleTagHelper;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;
import com.lab.utils.LogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.message.DiscussionNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by baziii on 15/8/7.
 *
 *
 * wtf
 * 1.fetch conversation list (attention : rongim conversation list return empty name);
 * 2.fetch conversation names and inject ; get oid finerid travelid
 * 3.filter avaliable conversations
 * 4.fetch avaliable conversations' user name &&ava &&service name
 * 5.build display item;
 */
public class ConversationFetcherRong implements IConversationsFetcher {
    UserNameAvaFetcher userNameAvaFetcher = new UserNameAvaFetcher();
    OrderNameFetcher orderNameFetcher = new OrderNameFetcher();

    @Override
    public void getConversations(final ListFetchCallback<ConversationItem> itemListFetchCallback) {
        //npl
        RongIM.getInstance().getRongIMClient().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(final List<Conversation> conversations) {
                AsyncTask asyncTask = new AsyncTask() {
                    List<ConversationItem> result = new ArrayList<>();

                    CountDownLatch countDownLatch;

                    @Override
                    protected Object doInBackground(Object[] params) {
                        LogHelper.e("fetch conversations", "start");
                        HashMap<String, Conversation> noTitleConversations = new HashMap<String, Conversation>();
                        if (conversations == null){
                            return null;
                        }
                        for (Conversation conversation : conversations) {
                            if (TextUtils.isEmpty(conversation.getConversationTitle()) ||
                                    !RongTitleTagHelper.isValidated(conversation.getConversationTitle())) {
                                noTitleConversations.put(conversation.getTargetId(), conversation);
                            }
                        }
                        LogHelper.e("fetch conversations", "detail " + noTitleConversations.size());
                        countDownLatch = new CountDownLatch(noTitleConversations.size());
                        for (final String key : noTitleConversations.keySet()) {
                            final Conversation conversation = noTitleConversations.get(key);
                            RongIM.getInstance().getRongIMClient().getDiscussion(key, new RongIMClient.ResultCallback<Discussion>() {
                                public void onSuccess(Discussion discussion) {
                                    LogHelper.e("fetch conversations", TextUtils.join("|", new
                                            String[]{key, discussion.getName()}));
                                    conversation.setConversationTitle(discussion.getName());
                                    countDownLatch.countDown();
                                }

                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    LogHelper.e("fetch conversations", key + "|error|" + errorCode);
                                    countDownLatch.countDown();
                                }
                            });
                        }
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //filter need display

                        HashMap<String, Conversation> displayConversations = new HashMap<>();
                        final HashMap<String, UserInfo> uids = new HashMap<>();
                        final HashMap<String, OrderItem> orders = new HashMap<>();
                        UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
                        String uid = userInfo.getUid();
                        boolean isTravel = userInfo.isTravel();
                        Conversation temp;
                        if (conversations != null) {
                            for (Conversation conversation : conversations) {
                                LogHelper.e("getTargetId", conversation.getTargetId() + "|" + conversation.getConversationTitle() + "|"
                                        + conversation.getObjectName() + "|" + conversation.getSenderUserName() + "|" + uid + "|" + RongTitleTagHelper.filterTravellerId(conversation.getConversationTitle())
                                        + "|" + RongTitleTagHelper.filterFinderId(conversation.getConversationTitle()));
                                if (isTravel &&
                                        !uid.equals(RongTitleTagHelper.filterTravellerId(conversation.getConversationTitle()))) {
                                    LogHelper.e("omg", "not travel");
                                    continue;
                                } else if (!isTravel &&
                                        !uid.equals(RongTitleTagHelper.filterFinderId(conversation.getConversationTitle()))) {
                                    LogHelper.e("omg", "not finder|" + RongTitleTagHelper.filterFinderId(conversation.getConversationTitle())
                                            + "  " + TextUtils.join("--", conversation.getConversationTitle().split("\\|")));
                                    continue;
                                }
                                if (displayConversations.containsKey(conversation.getConversationTitle())) {
                                    temp = displayConversations.get(conversation.getConversationTitle());
                                    displayConversations.put(conversation.getConversationTitle(),
                                            temp.getSentTime() > conversation.getSentTime() ? temp : conversation);
                                } else {
                                    displayConversations.put(conversation.getConversationTitle(), conversation);
                                }
                            }
                        }
                        for (Conversation conversation : displayConversations.values()) {
                            uids.put(RongTitleTagHelper.filterFinderId(conversation.getConversationTitle()), null);
                            uids.put(RongTitleTagHelper.filterTravellerId(conversation.getConversationTitle()), null);
                            orders.put(RongTitleTagHelper.filterOrderId(conversation.getConversationTitle()), null);
                        }
                        LogHelper.e("fetch conversations", "uname order" + uids.size());
                        countDownLatch = new CountDownLatch(uids.size() + orders.size());
                        for (final String id : uids.keySet()) {
                            userNameAvaFetcher.fetchUseNameAva(id, new CtFetchCallback<UserInfo>() {
                                @Override
                                public void onSuc(UserInfo userInfo) {
                                    uids.put(id, userInfo);
                                    countDownLatch.countDown();
                                    LogHelper.e("fetch conversations", "uname order " + id+"suc");
                                }

                                @Override
                                public void onFailed(CtException throwable) {
                                    countDownLatch.countDown();
                                    LogHelper.e("fetch conversations", "uname order" + id+"failed");
                                }
                            });
                        }
                        LogHelper.e("fetch conversations", "orders " + orders.size());
                        for (final String id : orders.keySet()) {
                            orderNameFetcher.fetcherOrderName(id, new CtFetchCallback<OrderItem>() {
                                @Override
                                public void onSuc(OrderItem orderItem) {
                                    orders.put(id, orderItem);
                                    countDownLatch.countDown();
                                    LogHelper.e("fetch conversations", " order" + id+"suc");
                                }

                                @Override
                                public void onFailed(CtException throwable) {
                                    countDownLatch.countDown();
                                    LogHelper.e("fetch conversations", " order" + id+"failed");
                                }
                            });
                        }
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        UserInfo finder;
                        UserInfo travel;

                        LogHelper.e("fetch conversations", "display  " + displayConversations.size());
                        //add nameAndImage
                        for (Conversation conversation : displayConversations.values()) {
                            ConversationItem item;
                            String itsName;
                            String itsAva;
                            String serviceName;
                            finder = uids.get(RongTitleTagHelper.filterFinderId(conversation.getConversationTitle()));
                            travel = uids.get(RongTitleTagHelper.filterTravellerId(conversation.getConversationTitle()));
                            itsName = isTravel ?
                                    getName(finder) : getName(travel);
                            itsAva = isTravel ?
                                    getAva(finder) : getAva(travel);
                            serviceName = geServicetName(orders.get(RongTitleTagHelper.filterOrderId(conversation.getConversationTitle())));
                            item = new ConversationItem(conversation.getTargetId(),
                                    RongTitleTagHelper.filterOrderId(conversation.getConversationTitle()),
                                    itsName,
                                    conversation.getUnreadMessageCount(),
                                    serviceName,
                                    String.valueOf(RongTitleTagHelper.buildDateString(conversation.getSentTime())),
                                    itsAva);

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

                                item.setLastWords(discussionNotificationMessage.getExtension());
                            } else if (conversation.getLatestMessage() instanceof ImageMessage) {
                                item.setLastWords("[图片]");
                            } else {
                                item.setLastWords("其他消息");
                            }
                            result.add(item);
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        itemListFetchCallback.onSuc(result);
                    }
                };
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        }, Conversation.ConversationType.DISCUSSION);
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
