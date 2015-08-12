package com.cuitrip.app.orderdetail.orderstatus.travel;

import android.os.AsyncTask;

import com.cuitrip.app.orderdetail.IFinderOrderDetailView;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.model.OrderItem;

/**
 * Created by baziii on 15/8/11.
 */
public class TravelWaitConfirmPresent extends BaseOrderFormPresent<IFinderOrderDetailView> {

    public TravelWaitConfirmPresent(IFinderOrderDetailView orderDetailView, OrderItem orderItem) {
        super(orderDetailView, orderItem);
    }

    @Override
    public void render() {
        mOrderDetailView.renderUi(build(mOrderItem));
    }

    @Override
    public void clickBottom() {
        mOrderDetailView.jumpConfirmOrder(mOrderItem);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mOrderItem.setStatus(OrderItem.STATUS_WAIT_PAY);
                mOrderDetailView.requestPresentRender(mOrderItem);
            }
        }.execute();
    }

    @Override
    public void clickMenu() {
        mOrderDetailView.jumpRefuseOrder(mOrderItem);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mOrderItem.setStatus(OrderItem.STATUS_UNVALIABLE);
                mOrderDetailView.requestPresentRender(mOrderItem);
            }
        }.execute();
    }

    @Override
    public String getBottomText(OrderItem orderItem) {
        return "确认预订";
    }

    @Override
    public String getMenuText(OrderItem orderItem) {
        return "拒绝";
    }

    @Override
    public boolean getBottomEnable(OrderItem orderItem) {
        return true;
    }

}