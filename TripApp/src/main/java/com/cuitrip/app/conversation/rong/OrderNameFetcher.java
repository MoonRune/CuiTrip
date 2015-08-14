package com.cuitrip.app.conversation.rong;

import android.os.AsyncTask;

import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/14.
 */
public class OrderNameFetcher {
    public void fetcherOrderName(final String id,final CtFetchCallback<OrderItem> fetchCallback){
        new AsyncTask() {
            OrderItem orderItem;
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    orderItem = new OrderItem();
                    orderItem.setServiceName("name" + id);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                fetchCallback.onSuc(orderItem);
                super.onPostExecute(o);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
