package com.cuitrip.app.pay;

import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.app.base.CtFetchCallback;

/**
 * Created by baziii on 15/8/15.
 */
public interface IPayOrderFetcher {
    void fetchPayOrder(String oid,CtFetchCallback<PayOrderMode> callback);
    void setDiscount(String oid,String discountCode,CtApiCallback callback);
    void removeDiscount(String oid,CtApiCallback callback);
    void getChar(String oid,String type,String ip,String currency, String couponId,CtFetchCallback<String> callback);
    void notifiOrderStatus(String oid,CtApiCallback callback);
}
