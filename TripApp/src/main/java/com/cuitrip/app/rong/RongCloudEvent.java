package com.cuitrip.app.rong;

import android.net.Uri;
import android.text.TextUtils;

import com.cuitrip.app.MainApplication;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

    public static void DisConnectRong(){
        RongIM.getInstance().disconnect();
    }

    public static void ConnectRongForce(){
        ConnectRong(true);
    }
    public static void ConnectRong(boolean force) {
        if (!LoginInstance.isLogin(MainApplication.getInstance())) {
            return;
        }
        com.cuitrip.model.UserInfo userInfo = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getUid())) {
            return;
        }
        if (TextUtils.isEmpty(userInfo.getRongyunToken())){
            LogHelper.e(TAG,"rongyun roken null");
            if (force) {
                MainApplication.getInstance().logOutWithError();
            }
            return;
        }
        String token = userInfo.getRongyunToken();
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

    AsyncHttpClient mClient = new AsyncHttpClient();

    @Override
    public UserInfo getUserInfo(final String userId) {
        LogHelper.e("UserInfoProvider", "search uid :" + userId);
        Uri uri = Uri.parse("http://cdn.aixifan.com/dotnet/artemis/u/cms/www/201508/07094633g8p80txx.jpg");
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<UserInfo> result = new ArrayList<>();
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
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return new UserInfo(userId, "unknown" + userId, uri);
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
