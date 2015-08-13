package com.cuitrip.app.rong;

import com.alibaba.fastjson.JSON;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.utils.LogHelper;

import java.util.Date;

import io.rong.imkit.util.RongDateUtils;

/**
 * Created by baziii on 15/8/12.
 */
public class RongTitleTagHelper {

    public static class TitleMessage {
        String orderId;
        String travellerId;
        String travellerAva;
        String finderId;
        String finderAva;
        String serviceName;

        public String getOrderId() {
            return orderId;
        }

        public String getTravellerId() {
            return travellerId;
        }

        public String getTravellerAva() {
            return travellerAva;
        }

        public String getFinderId() {
            return finderId;
        }

        public String getFinderAva() {
            return finderAva;
        }

        public String getServiceName() {
            return serviceName;
        }

        public TitleMessage() {
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public void setTravellerId(String travellerId) {
            this.travellerId = travellerId;
        }

        public void setTravellerAva(String travellerAva) {
            this.travellerAva = travellerAva;
        }

        public void setFinderId(String finderId) {
            this.finderId = finderId;
        }

        public void setFinderAva(String finderAva) {
            this.finderAva = finderAva;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public TitleMessage(String orderId, String travellerId, String travellerAva, String finderId, String finderAva, String serviceName) {
            this.orderId = orderId;
            this.travellerId = travellerId;
            this.travellerAva = travellerAva;
            this.finderId = finderId;
            this.finderAva = finderAva;
            this.serviceName = serviceName;
        }

        public String buildJson() {
            return JSON.toJSONString(this);
        }

        public static TitleMessage getInstance(String title) {
            TitleMessage data = null;
            try {
                data = JSON.parseObject(title, TitleMessage.class);
            } catch (Exception e) {
                LogHelper.e("omg","pasererror:"+title+"|"+e.getMessage());
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
        return new TitleMessage(orderItem.getOid(), orderItem.getTravellerId(),
                orderItem.getTravellerName(), orderItem.getInsiderId(), orderItem.getInsiderName(),
                orderItem.getServiceName()).buildJson();
    }

    public static String filterServiceName(String title) {
        TitleMessage msg = TitleMessage.getInstance(title);
        if (msg != null) {
            return msg.getServiceName();
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


    public static String filterFinderId(String title) {
        TitleMessage msg = TitleMessage.getInstance(title);
        if (msg != null) {
            return msg.getFinderId();
        }
        return PlatformUtil.getInstance().getString(R.string.ct_discussion_unsupport);
    }

    public static String buildDateString(long date) {
       return  RongDateUtils.getConversationListFormatDate(new Date(date));
    }
}
