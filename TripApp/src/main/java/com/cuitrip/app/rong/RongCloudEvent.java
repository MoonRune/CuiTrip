package com.cuitrip.app.rong;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.cuitrip.app.IndexActivity;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.ImageActivity;
import com.cuitrip.app.map.GaoDeMapActivity;
import com.cuitrip.app.orderdetail.OrderFormActivity;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.OnReceivePushMessageListener;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.notification.PushNotificationMessage;

/**
 * Created by baziii on 15/8/11.
 */
public class RongCloudEvent implements RongIM.UserInfoProvider, RongIMClient.OnReceiveMessageListener, RongIM.ConversationBehaviorListener,
        RongIMClient.ConnectionStatusListener, OnReceivePushMessageListener {
    public static final String TAG = "RongCloudEvent";

    public static final int DEFAULT_CHECK_MODE_NOTIFCATION_ID = 2;
    public static final int DEFAULT_ERROR_NOTIFCATION_ID = 3;

    private RongCloudEvent() {

    }

    private static RongCloudEvent sRongCloudEvent;

    public static RongCloudEvent getInstance() {
        if (sRongCloudEvent == null) {
            synchronized (RongCloudEvent.class) {
                if (sRongCloudEvent == null) {
                    sRongCloudEvent = new RongCloudEvent();
                }
            }
        }
        return sRongCloudEvent;
    }

    //query for userinfo  from api && local  ??

    public static void DisConnectRong() {
        RongIM.getInstance().disconnect();
    }

    public static void ConnectRongForce() {
        ConnectRong(true);
    }

    public static void ConnectRong(boolean force) {
        try {
            if (!LoginInstance.isLogin(MainApplication.getInstance())) {
                return;
            }
            com.cuitrip.model.UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
            if (userInfo == null || TextUtils.isEmpty(userInfo.getUid())) {
                return;
            }
            if (TextUtils.isEmpty(userInfo.getRongyunToken())) {
                LogHelper.e(TAG, "rongyun roken null");
                if (force) {
                    MainApplication.getInstance().logOutWithError();
                }
                return;
            }
            String token = userInfo.getRongyunToken();
            LogHelper.e(TAG, "rongyun roken is : " + token);
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                @Override
                public void onSuccess(String userId) {
                    LogHelper.e("ron suc", "" + userId);
    /* 连接成功 */
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {
                    LogHelper.e("ron failed", "");
    /* 连接失败，注意并不需要您做重连 */
                }

                @Override
                public void onTokenIncorrect() {
                    LogHelper.e("ron token error", "");
                    try {
                        MainApplication.getInstance().logOutWithError();
                    } catch (Exception e) {
                    }
                }

            });
        } catch (Exception e) {
            MessageUtils.showToast(R.string.load_error);
        }
    }

    SyncHttpClient mClient = new SyncHttpClient();

    @Override
    public UserInfo getUserInfo(final String userId) {
        LogHelper.e("UserInfoProvider", "search uid :" + userId);
        Uri uri = Uri.parse("http://mmbiz.qpic.cn/mmbiz/CCOz0VqjicmxJpUWy6iaibsJ0FcIGaHDLo0TqBHVzyEJOmeaia8mW6jmBnsUrfSJNyd7vAf4sgc9U7ZJ4ydEicNpvZA/0?wx_fmt=gif&wxfrom=5&wx_lazy=1");
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<UserInfo> result = new ArrayList<>();
        try {
            if (LoginInstance.isLogin(MainApplication.getInstance())) {
                UserBusiness.getUserInfo(MainApplication.getInstance(), mClient, new LabAsyncHttpResponseHandler(com.cuitrip.model.UserInfo.class) {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        if (data != null && data instanceof com.cuitrip.model.UserInfo) {
                            com.cuitrip.model.UserInfo temp = ((com.cuitrip.model.UserInfo) data);
                            result.add(new UserInfo(userId, temp.getNick(), Uri.parse(temp.getHeadPic())));
                        }
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        countDownLatch.countDown();
                    }
                }, userId);
                countDownLatch.await();
            }
        } catch (Exception e) {
            LogHelper.e("UserInfoProvider", "search error " + e.getMessage());
            e.printStackTrace();
        }
        if (result.size() > 0) {
            LogHelper.e("UserInfoProvider", "search name : " + result.get(0).getName());
            return result.get(0);
        }
        LogHelper.e("UserInfoProvider", "search name : null");
        UserInfo empty = new UserInfo(userId, PlatformUtil.getInstance().getString(R.string.loading_text), uri);
        return empty;
    }

    @Override
    public boolean onReceived(Message message, int left) {
        LogHelper.e(TAG, "on receive message" + message.getTargetId());
        if (OrderFormActivity.CURRENT_TARGET == null || !OrderFormActivity.CURRENT_TARGET.equals(message.getTargetId())) {
            LogHelper.e(TAG, "on receive message query");
            try {
                com.cuitrip.model.UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
                String content = getMessageContent(message.getContent());
                if (!TextUtils.isEmpty(content)) {
                    queryForInfo(MainApplication.getInstance(), message, content, userInfo, false);
                } else {
                    LogHelper.e(TAG, " seems should not see " + message.getObjectName() + "|" + message.toString());
                }
            } catch (Exception e) {
                LogHelper.e(TAG, " error " + e.getMessage());
            }
        }
        return true;

    }

    public static String getMessageContent(MessageContent messageContent) {

        String content = "";
        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            content = textMessage.getContent();
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            content = PlatformUtil.getInstance().getString(R.string.im_default_message_image);
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            content = PlatformUtil.getInstance().getString(R.string.im_default_message_voice);
        } else if (messageContent instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            content = PlatformUtil.getInstance().getString(R.string.im_default_message_rich_text);
        } else if (messageContent instanceof LocationMessage) {//图文消息
            LocationMessage location = (LocationMessage) messageContent;
            content = PlatformUtil.getInstance().getString(R.string.im_default_message_location_with_string_in, location.getPoi());
        } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息
            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
            LogHelper.e(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
            content = PlatformUtil.getInstance().getString(R.string.im_default_message_notificataion);
        } else {
            if (messageContent == null) {
                content = PlatformUtil.getInstance().getString(R.string.send_message_with_name_below, "");
            } else {
                content = null;
            }
            LogHelper.e("omg", " receive push message other" + messageContent);
//            content = "[通知]";
        }
        return content;
    }

    AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    public void queryForInfo(final Context context, final Message message, final String content, final com.cuitrip.model.UserInfo me, boolean async) {
        LogHelper.e("queryForInfo target ", "" + message.getTargetId());
        OrderBusiness.getOrderByRongTargetId(context, async ? mAsyncHttpClient : mClient, new LabAsyncHttpResponseHandler(OrderItem.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null && data instanceof OrderItem) {
                    OrderItem orderItem = ((OrderItem) data);
                    try {

                        if ((me.isTravel() && me.getUid().equals(orderItem.getTravellerId()))
                                || ((!me.isTravel()) && me.getUid().equals(orderItem.getInsiderId()))) {
                            String title = "";
                            String contentLittle = context.getString(R.string.send_message_with_name_below, "");
                            if (!TextUtils.isEmpty(orderItem.getUserNick())) {
                                title = orderItem.getUserNick();
                                if (!TextUtils.isEmpty(title)) {
                                    contentLittle = context.getString(R.string.send_message_with_name_below, title);
                                }
                            }
                            buildNotification(message.getTargetId().hashCode(),
                                    orderItem.getUserNick(), content, contentLittle, orderItem.getOid());
                            return;
                        }
                        buildNotification(DEFAULT_CHECK_MODE_NOTIFCATION_ID, context.getString(R.string.switch_mode_to_watch), content, context.getString(R.string.switch_mode_to_watch), null);

                    } catch (Exception e) {
                        buildNotification(DEFAULT_ERROR_NOTIFCATION_ID, context.getString(R.string.unknown) + e.getMessage(), content, context.getString(R.string.send_message_with_name_below, ""), null);
                    }
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                buildNotification(DEFAULT_ERROR_NOTIFCATION_ID, context.getString(R.string.unknown), content, context.getString(R.string.send_message_with_name_below, ""), null);

            }
        }, message.getTargetId());
    }

    public void buildNotification(int id, String content, String title, String contentLittle, String oid) {
        LogHelper.e(TAG, TextUtils.join(" | ", new String[]{String.valueOf(id), content, title, contentLittle, oid}));
        //定义NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) MainApplication.getInstance().getSystemService(ns);
        //定义通知栏展现的内容信息
        int icon = R.drawable.ct_ic_launcher;
        long when = System.currentTimeMillis();

        Notification.Builder builder = new Notification.Builder(MainApplication.getInstance());
        builder.setSmallIcon(icon);
        builder.setTicker(contentLittle);
        builder.setWhen(when);

        //定义下拉通知栏时要展现的内容信息
        CharSequence contentTitle = title;
        CharSequence contentText = content;
        Intent notificationIntent = new Intent(MainApplication.getInstance(), IndexActivity.class);
        if (!TextUtils.isEmpty(oid)) {
            notificationIntent = OrderFormActivity.getStartIntent(MainApplication.getInstance(), oid);
        }
        notificationIntent.setFlags(notificationIntent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(MainApplication.getInstance(), id,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setAutoCancel(true);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.getNotification();
        try {
            if (!TextUtils.isEmpty(Utils.getSystemProperty("ro.miui.ui.version.name"))) {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(id, notification);
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus) {
            case DISCONNECTED:
                MessageUtils.showToast(PlatformUtil.getInstance().getString(R.string.im_status_disconnected));
                break;
            case CONNECTED:
                MessageUtils.showToast(PlatformUtil.getInstance().getString(R.string.im_status_connected));
                break;
            case CONNECTING:
                MessageUtils.showToast(PlatformUtil.getInstance().getString(R.string.im_status_connecting));
                break;
            case NETWORK_UNAVAILABLE:
                MessageUtils.showToast(PlatformUtil.getInstance().getString(R.string.im_status_network_unavaliable));
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:
                MessageUtils.showToast(PlatformUtil.getInstance().getString(R.string.im_status_kick_off));
                break;
        }
    }

    @Override
    public boolean onReceivePushMessage(final PushNotificationMessage message) {
        LogHelper.e(TAG, "onReceivePushMessage");

        try {
            final com.cuitrip.model.UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();

            if (message.getContent() != null) {
                String content = getMessageContent(message.getContent());
                if (!TextUtils.isEmpty(content)) {
                    LogHelper.e(TAG, "content  not empty");

                    queryForInfo(MainApplication.getInstance(), message, content, userInfo, true);
                } else {
                    LogHelper.e(TAG, " seems should not see " + String.valueOf(message.getContent()));
                }
            } else {
                LogHelper.e(TAG, "message.getContent() empty");
                String token = userInfo.getRongyunToken();
                LogHelper.e(TAG, "rongyun roken is : " + token);
                RongIM.connect(token, new RongIMClient.ConnectCallback() {

                    @Override
                    public void onSuccess(String userId) {
                        LogHelper.e("ron suc", "" + userId);
                        RongIMClient.getInstance().getConversation(
                                Conversation.ConversationType.DISCUSSION, message.getTargetId(), new RongIMClient.ResultCallback<Conversation>() {
                                    @Override
                                    public void onSuccess(Conversation conversation) {
                                        if (conversation != null) {
                                            LogHelper.e(TAG, "suc ");
                                            message.setContent(conversation.getLatestMessage());
                                            String content = getMessageContent(message.getContent());
                                            if (!TextUtils.isEmpty(content)) {
                                                LogHelper.e(TAG, "content  not empty");

                                                queryForInfo(MainApplication.getInstance(), message, content, userInfo, true);
                                            } else {
                                                LogHelper.e(TAG, " seems should not see " + String.valueOf(message.getContent()));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        LogHelper.e(TAG, "failed " + errorCode);

                                    }
                                }
                        );
    /* 连接成功 */
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {
                        LogHelper.e("ron failed", "");
    /* 连接失败，注意并不需要您做重连 */
                    }

                    @Override
                    public void onTokenIncorrect() {
                        LogHelper.e("ron token error", "");
    /* Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token */
                    }

                });

            }
        } catch (Exception e) {
            LogHelper.e(TAG, " error " + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return true;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        LogHelper.e("omg", "onMessageClick" + context.getClass().getName());
        if (message != null && message.getContent() != null) {
            if (message.getContent() instanceof LocationMessage) {
                LocationMessage locationMessage = ((LocationMessage) message.getContent());
                LogHelper.e("omg", "LocationMessage" + locationMessage.getLat() + "|" + locationMessage.getLng());
//                BillActivity.start(context);
                GaoDeMapActivity.startShow(context, locationMessage.getLat(), locationMessage.getLng(), locationMessage.getPoi());
                LogHelper.e("omg", "LocationMessage end");
                return true;
            } else if (message.getContent() instanceof ImageMessage) {
                ImageMessage imageMessage = ((ImageMessage) message.getContent());
//                Intent intent = new Intent();
//                intent.setAction(android.content.Intent.ACTION_VIEW);
//                intent.setDataAndType(imageMessage.getLocalUri(), "image/*");
//                context.startActivity(intent);
                ImageActivity.start(context, imageMessage.getRemoteUri());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMessageLinkClick(String s) {
        LogHelper.e("omg", "onMessageLinkClick");
        return true;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return true;
    }


}
