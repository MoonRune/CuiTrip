package com.cuitrip.app.conversation.rong;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.ListFetchCallback;
import com.cuitrip.app.conversation.ConversationItem;
import com.cuitrip.app.conversation.IConversationsFetcher;
import com.cuitrip.app.rong.RongTitleTagHelper;
import com.cuitrip.login.LoginInstance;
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
 */
public class ConversationFetcherRong implements IConversationsFetcher {


    @Override
    public void getConversations(final ListFetchCallback<ConversationItem> itemListFetchCallback) {
        RongIM.getInstance().getRongIMClient().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(final List<Conversation> conversations) {
                AsyncTask asyncTask = new AsyncTask() {
                    List<ConversationItem> result = new ArrayList<>();

                    CountDownLatch countDownLatch;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
//                        LogHelper.e("omg", "" + (RongContext.getInstance().getCurrentConversationList() == null ?
//                                "null" : String.valueOf(RongContext.getInstance().getCurrentConversationList().size())));
//                        if (RongContext.getInstance().getCurrentConversationList()!=null){
//                            for (ConversationInfo conversation :RongContext.getInstance().getCurrentConversationList()){
//                                LogHelper.e("omg"," in context "+conversation.getTargetId() + "|" + conversation.getConversationTitle());
//                            }
//                        }


                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        HashMap<String, Conversation> noTitleConversations = new HashMap<String, Conversation>();
                        for (Conversation conversation : conversations) {
                            ConversationItem item;

                            if (TextUtils.isEmpty(conversation.getConversationTitle())) {
                                noTitleConversations.put(conversation.getTargetId(), conversation);
                            }
                        }
                        countDownLatch = new CountDownLatch(noTitleConversations.size());
                        for (final String key : noTitleConversations.keySet()) {
                            final Conversation conversation = noTitleConversations.get(key);
                            RongIM.getInstance().getRongIMClient().getDiscussion(key, new RongIMClient.ResultCallback<Discussion>() {
                                public void onSuccess(Discussion discussion) {
                                    conversation.setConversationTitle(discussion.getName());
                                    countDownLatch.countDown();
                                }


                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    countDownLatch.countDown();
                                }
                            });
                        }
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
                        String uid = userInfo.getUid();
                        boolean isTravel = userInfo.isTravel();
                        if (conversations != null) {
                            for (Conversation conversation : conversations) {
                                ConversationItem item;
                                LogHelper.e("getTargetId", conversation.getTargetId() + "|" + conversation.getConversationTitle() + "|"
                                        + conversation.getObjectName() + "|" + conversation.getSenderUserName() + "|" + uid + "|" + RongTitleTagHelper.filterTravellerId(conversation.getConversationTitle())
                                        + "|" + RongTitleTagHelper.filterFinderId(conversation.getConversationTitle()));

                                LogHelper.e("omg", "" + isTravel + "|" + (!uid.equals(RongTitleTagHelper.filterTravellerId(conversation.getConversationTitle()))) +
                                        "|" + !uid.equals(RongTitleTagHelper.filterFinderId(conversation.getConversationTitle())));
                                if (isTravel &&
                                        !uid.equals(RongTitleTagHelper.filterTravellerId(conversation.getConversationTitle()))) {
                                    LogHelper.e("omg", "i am travel but order belong travel");
                                    continue;
                                } else if (!isTravel &&
                                        !uid.equals(RongTitleTagHelper.filterFinderId(conversation.getConversationTitle()))) {
                                    LogHelper.e("omg", "i am finder but order belong finder");
                                    continue;

                                }
                                LogHelper.e("omg", "add");

                                if (conversation.getLatestMessage() instanceof VoiceMessage) {
                                    VoiceMessage voiceMessage = (VoiceMessage) conversation.getLatestMessage();
                                    item = new ConversationItem(conversation.getTargetId(),
                                            RongTitleTagHelper.filterServiceName(conversation.getConversationTitle()),
                                            "[voice]",
                                            String.valueOf(RongTitleTagHelper.buildDateString(conversation.getSentTime()))
                                    );
                                } else if (conversation.getLatestMessage() instanceof TextMessage) {
                                    TextMessage textMessage = (TextMessage) conversation.getLatestMessage();
                                    item = new ConversationItem(conversation.getTargetId(),
                                            RongTitleTagHelper.filterServiceName(conversation.getConversationTitle()),
                                            textMessage.getContent(),
                                            String.valueOf(RongTitleTagHelper.buildDateString(conversation.getSentTime()))
                                    );
                                } else if (conversation.getLatestMessage() instanceof RichContentMessage) {
                                    RichContentMessage richContentMessage = (RichContentMessage) conversation.getLatestMessage();
                                    item = new ConversationItem(conversation.getTargetId(),
                                            RongTitleTagHelper.filterServiceName(conversation.getConversationTitle()),
                                            richContentMessage.getContent(),
                                            String.valueOf(RongTitleTagHelper.buildDateString(conversation.getSentTime()))
                                    );
                                } else if (conversation.getLatestMessage() instanceof DiscussionNotificationMessage) {
                                    DiscussionNotificationMessage discussionNotificationMessage = (DiscussionNotificationMessage) conversation.getLatestMessage();
                                    item = new ConversationItem(conversation.getTargetId(),
                                            RongTitleTagHelper.filterServiceName(conversation.getConversationTitle()),
                                            discussionNotificationMessage.getExtension(),
                                            String.valueOf(RongTitleTagHelper.buildDateString(conversation.getSentTime()))
                                    );
                                }else if (conversation.getLatestMessage() instanceof ImageMessage) {
                                    item = new ConversationItem(conversation.getTargetId(),
                                            RongTitleTagHelper.filterServiceName(conversation.getConversationTitle()),
                                            "[图片]",
                                            String.valueOf(conversation.getSentTime())
                                    );
                                } else {
                                    item = new ConversationItem(conversation.getTargetId(),
                                            RongTitleTagHelper.filterServiceName(conversation.getConversationTitle()),
                                            conversation.getLatestMessage() != null ? conversation.getLatestMessage().toString() : "",
                                            String.valueOf(RongTitleTagHelper.buildDateString(conversation.getSentTime()))
                                    );
                                }
                                result.add(item);
                            }
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
}
