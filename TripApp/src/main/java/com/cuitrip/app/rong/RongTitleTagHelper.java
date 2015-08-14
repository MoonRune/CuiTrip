package com.cuitrip.app.rong;

import android.text.TextUtils;

import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.utils.LogHelper;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.HashMap;

import io.rong.imkit.util.RongDateUtils;

/**
 * Created by baziii on 15/8/12.
 */
public class RongTitleTagHelper {

    public static HashMap<Integer, SoftReference<TitleMessage>> SCache = new HashMap<>();

    public static final String SPLIT_ADD = "|";

    public static final String SPLIT_BY="\\|";
    public static class TitleMessage {
        public static final int SIZE = 3;
        String orderId;
        String travellerId;
        String finderId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getTravellerId() {
            return travellerId;
        }

        public void setTravellerId(String travellerId) {
            this.travellerId = travellerId;
        }

        public String getFinderId() {
            return finderId;
        }

        public void setFinderId(String finderId) {
            this.finderId = finderId;
        }


        public TitleMessage() {
        }

        public TitleMessage(String orderId, String travellerId, String finderId) {
            this.orderId = orderId;
            this.travellerId = travellerId;
            this.finderId = finderId;
        }

        public String buildString() {
            return TextUtils.join(SPLIT_ADD, new String[]{orderId, travellerId, finderId});
        }

        public static TitleMessage getInstance(String title) {
            if (title == null) {
                return null;
            }
            int key = title.hashCode();
            if (SCache.containsKey(key) && SCache.get(key).get() != null) {
                return SCache.get(key).get();
            }
            TitleMessage data = null;
            try {
                String[] values = TextUtils.split(title,SPLIT_BY);
                data = new TitleMessage();
                data.setOrderId(values[0]);
                data.setTravellerId(values[1]);
                data.setFinderId(values[2]);
            } catch (Exception e) {
                LogHelper.e("omg", "pasererror:" + title + "|" + e.getMessage());
                return null;
            }
            return data;
        }
    }

    public static boolean isBelongToOrder(String title, OrderItem order) {
        TitleMessage msg = TitleMessage.getInstance(title);
        if (msg != null) {
            return order.getOid().equals(msg.getOrderId());
        }
        return false;

    }

    public static String buildTitle(OrderItem orderItem) {
        return new TitleMessage(orderItem.getOid(),
                orderItem.getTravellerId(),
                orderItem.getInsiderId()).buildString();
    }

    public static String filterOrderId(String title) {
        TitleMessage msg = TitleMessage.getInstance(title);
        if (msg != null) {
            return msg.getOrderId();
        }
        return PlatformUtil.getInstance().getString(R.string.ct_discussion_unsupport);
    }

    public static String filterTravellerId(String title) {
        TitleMessage msg = TitleMessage.getInstance(title);
        if (msg != null) {
            return msg.getTravellerId();
        }
        return PlatformUtil.getInstance().getString(R.string.ct_discussion_unsupport);
    }


    public static boolean isValidated(String title) {
        return TitleMessage.getInstance(title) != null;
    }

    public static String filterFinderId(String title) {
        TitleMessage msg = TitleMessage.getInstance(title);
        if (msg != null) {
            return msg.getFinderId();
        }
        return PlatformUtil.getInstance().getString(R.string.ct_discussion_unsupport);
    }

    public static String buildDateString(long date) {
        return RongDateUtils.getConversationListFormatDate(new Date(date));
    }
}
