package com.cuitrip.app.rong;

import android.net.Uri;
import android.text.TextUtils;

import com.cuitrip.app.MainApplication;
import com.cuitrip.login.LoginInstance;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;

import java.util.HashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by baziii on 15/8/11.
 */
public class RongCloudEvent implements RongIM.UserInfoProvider, RongIMClient.OnReceiveMessageListener,
        RongIMClient.ConnectionStatusListener {
    public static final String TAG = "RongCloudEvent";
    //query for userinfo  from api && local  ??
    static HashMap<String, String> tempUidToken = new HashMap<>();

    static {
        tempUidToken.put("180", "iOYYGEULMn9cEFHJvU2KclUJjq/G011LMurM5xSobnWdFPeJcpSCKXQDIt1DpCe4kC4zt6Ilvdl1WC8KOcAiRg==");
        tempUidToken.put("179", "anTVf0l/BWsxuHHSVIS9QVUJjq/G011LMurM5xSobnWdFPeJcpSCKaeojIan8MAUglmpfkJTWhp1WC8KOcAiRg==");
    }

    public static void ConnectRong() {
        if (!LoginInstance.isLogin(MainApplication.getInstance())) {
            return;
        }
        com.cuitrip.model.UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getUid())) {
            return;
        }
        String token = tempUidToken.get(userInfo.getUid());
        if (TextUtils.isEmpty(token)) {
            return;
        }
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
/* Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token */
            }

        });
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        LogHelper.e("UserInfoProvider", "search uid :" + userId);
        Uri uri = Uri.parse("http://cdn.aixifan.com/dotnet/artemis/u/cms/www/201508/07094633g8p80txx.jpg");
        return new UserInfo(userId, "name" + userId, uri);
    }

    @Override
    public boolean onReceived(Message message, int left) {
        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            LogHelper.e(TAG, "onReceived-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            LogHelper.e(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            LogHelper.e(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            LogHelper.e(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
        } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息
            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
            LogHelper.e(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
//            if (DemoContext.getInstance() != null)
//                getFriendByUserIdHttpRequest = DemoContext.getInstance().getDemoApi().getUserInfoByUserId(message.getSenderUserId(), (ApiCallback<User>) this);
//        } else if (messageContent instanceof DeAgreedFriendRequestMessage) {//好友添加成功消息
//            DeAgreedFriendRequestMessage deAgreedFriendRequestMessage = (DeAgreedFriendRequestMessage) messageContent;
//            LogHelper.e(TAG, "onReceived-deAgreedFriendRequestMessage:" + deAgreedFriendRequestMessage.getMessage());
//            receiveAgreeSuccess(deAgreedFriendRequestMessage);
//        } else if (messageContent instanceof ContactNotificationMessage) {//好友添加消息
//            ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
//            LogHelper.e(TAG, "onReceived-ContactNotificationMessage:getExtra;" + contactContentMessage.getExtra());
//            LogHelper.e(TAG, "onReceived-ContactNotificationMessage:+getmessage:" + contactContentMessage.getMessage().toString());
//            Intent in = new Intent();
//            in.setAction(MainActivity.ACTION_DMEO_RECEIVE_MESSAGE);
//            in.putExtra("rongCloud", contactContentMessage);
//            in.putExtra("has_message", true);
//            mContext.sendBroadcast(in);
        } else {
            LogHelper.e(TAG, "onReceived-其他消息，自己来判断处理");
        }

        return false;

    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus) {
            case DISCONNECTED:
                MessageUtils.showToast("失去连接");
                break;
            case CONNECTED:
                MessageUtils.showToast("已连接");
                break;
            case CONNECTING:
                MessageUtils.showToast("正在连接中");
                break;
            case NETWORK_UNAVAILABLE:
                MessageUtils.showToast("网络差");
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:
                MessageUtils.showToast("在其他设别上登录");
                break;
        }
    }
}
