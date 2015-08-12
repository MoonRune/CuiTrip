package com.cuitrip.app.rong;

import android.text.TextUtils;

import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;

/**
 * Created by baziii on 15/8/12.
 */
public class RongTitleTagHelper {
    
    public static final int SIZE =4;
    public static boolean isBelongToOrder(String title,OrderItem order){
        if (TextUtils.isEmpty(title)){
            return false;
        }
        String[] splited = title.split("/");
        if (splited != null && splited.length == SIZE) {
            return splited[3].equals(order.getOid());
        }
        return false;
        
    }
    public static String buildTitle(OrderItem orderItem) {
        return orderItem.getTravellerId() + "/" + orderItem.getInsiderId() + "/" + orderItem.getServiceName() + "/" + orderItem.getOid();
    }

    public static String filterServiceName(String title) {
        String[] splited = title.split("/");
        if (splited != null && splited.length == SIZE) {
            return splited[2];
        }
        return PlatformUtil.getInstance().getString(R.string.ct_discussion_unsupport);
    }

    public static String filterTravellerId(String title) {
        if (TextUtils.isEmpty(title)){
            return "";
        }
        String[] splited = title.split("/");
        if (splited != null && splited.length == SIZE) {
            return splited[0];
        }
        return PlatformUtil.getInstance().getString(R.string.ct_discussion_unsupport);
    }


    public static String filterFinderId(String title) {
        if (TextUtils.isEmpty(title)){
            return "";
        }
        String[] splited = title.split("/");
        if (splited != null && splited.length == SIZE) {
            return splited[1];
        }
        return PlatformUtil.getInstance().getString(R.string.ct_discussion_unsupport);
    }

    public static String buildDateString(long date){

        return "";
    }
}
