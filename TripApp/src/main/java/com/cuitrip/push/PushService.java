package com.cuitrip.push;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.cuitrip.app.IndexActivity;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.orderdetail.OrderFormActivity;
import com.cuitrip.service.R;
import com.lab.utils.LogHelper;
import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import java.util.Map;
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


    private static final String UMENG_MESSAGE_ID = "messageId";

    private static final String UMENG_MESSAGE_ACTION = "goto";

    private static final String UMENG_MESSAGE_ACTION_ORDER_DETAIL = "orderDetail";

    @Override
    protected void onMessage(Context context, Intent intent) {
        LogHelper.e(TAG, "onMessage: " + intent.toString());
        super.onMessage(context, intent);
        try {
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            LogHelper.e(TAG, "onMessage: " + message);
            UMessage msg = new UMessage(new JSONObject(message));
            UTrack.getInstance(context).trackMsgClick(msg);
            updateMsg(context, msg);
            LogHelper.e(TAG, "onMessage: end");
        } catch (Exception e) {
            LogHelper.e(TAG, e.getMessage());
        }
    }

    private void updateMsg(Context context, UMessage msg) {
        if (msg.extra != null && !TextUtils.isEmpty(msg.extra.get("gmtCreated"))) {
            MessagePrefs.saveLastMessageTime(msg.extra.get("gmtCreated"));
        }
        MessagePrefs.setHasNewMessage(true);
        showNotification(context, msg);
        context.sendBroadcast(new Intent(NEW_MESSAGE_BROADCAT));
    }

    private void showNotification(Context context, UMessage msg) {
        Intent intent = new Intent(this, IndexActivity.class);
        intent.putExtra(IndexActivity.GO_TO_TAB, IndexActivity.ORDER_TAB);
        LogHelper.e("showNotification", "ORDER_TAB");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = getPending(msg);
        if (msg == null) {
            pendingIntent = PendingIntent.getActivity(context, new Random().nextInt() % 10000, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ct_ic_launcher)
                .setTicker(msg.title).setContentTitle(msg.title).setContentText(msg.text)
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(new Random().nextInt() % 10000, notification);
    }

    public PendingIntent getPending(UMessage message) {
        LogHelper.e(TAG, "getPending");
        PendingIntent result = null;
        try {
            Map<String,String> valus = message.extra;
            if (valus == null){

                LogHelper.e(TAG, "no valus");
            }
            if (valus != null  &&valus.containsKey(UMENG_MESSAGE_ACTION)) {

                LogHelper.e(TAG, "has value");
                switch (valus.get(UMENG_MESSAGE_ACTION)) {
                    case UMENG_MESSAGE_ACTION_ORDER_DETAIL:
                        if (!valus.containsKey(UMENG_MESSAGE_ID)){
                            LogHelper.e(TAG, "no messageId");
                            break;
                        }
                        String oid = valus.get(UMENG_MESSAGE_ID);
                        LogHelper.e(TAG, "has value " + UMENG_MESSAGE_ACTION_ORDER_DETAIL+"|"+oid);
                        Intent notificationIntent = OrderFormActivity.getStartIntent(MainApplication.getInstance(), oid);
                        notificationIntent.setFlags(notificationIntent.FLAG_ACTIVITY_NEW_TASK);
                        result = PendingIntent.getActivity(MainApplication.getInstance(), oid.hashCode(),
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        break;
                }
            }
        } catch (Exception e) {

            LogHelper.e(TAG, " JSON error "+e.getMessage());
        }
        return result;
    }
}
