package com.cuitrip.push;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.cuitrip.app.IndexActivity;
import com.cuitrip.service.R;
import com.lab.utils.LogHelper;
import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created on 7/19.
 */
public class PushService extends UmengBaseIntentService {
    private static final String TAG = "PushService";
    private static int MESSAGE_ID = 12345;

// 如果需要打开Activity，请调用Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)；否则无法打开Activity。

    public static final String NEW_MESSAGE_BROADCAT = "ct_new_message_coming";
    public static final String NEW_MESSAGE_TYPE = "ct_new_message_type";

    @Override
    protected void onMessage(Context context, Intent intent) {
        LogHelper.d(TAG, "onMessage: " + intent.toString());
        super.onMessage(context, intent);
        try {
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            LogHelper.d(TAG, "onMessage: " + message);
            UMessage msg = new UMessage(new JSONObject(message));
            UTrack.getInstance(context).trackMsgClick(msg);
            updateMsg(context, msg);
        } catch (Exception e) {
            LogHelper.e(TAG, e.getMessage());
        }
    }

    private void updateMsg(Context context, UMessage msg){
        if(msg.extra != null && !TextUtils.isEmpty(msg.extra.get("gmtCreated"))){
            MessagePrefs.saveLastMessageTime(msg.extra.get("gmtCreated"));
        }
        MessagePrefs.setHasNewMessage(true);
        showNotification(context, msg);
        context.sendBroadcast(new Intent(NEW_MESSAGE_BROADCAT));
    }

    private void showNotification(Context context, UMessage msg) {
        Intent intent = new Intent(this, IndexActivity.class);
        intent.putExtra(IndexActivity.GO_TO_TAB, IndexActivity.ORDER_TAB);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ct_ic_launcher)
                .setTicker(msg.title).setContentTitle(msg.title).setContentText(msg.text)
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(new Random().nextInt() % 10000, notification);
    }
}
