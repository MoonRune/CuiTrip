package com.cuitrip.app.orderdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseFragment;

/**
 * Created by baziii on 15/8/11.
 */
public class OrderFormFragment extends BaseFragment {

    public static final String TAG = "OrderFormFragment";
    public static final String ORDER_KEY = "OrderFormFragment.ORDER_KEY";
    MenuItem mOptionMenuItem;
    String mActionTitle;
    IOrderDetailView mOrderDetailView;

    public static OrderFormFragment newInstance(OrderItem orderItem) {
        Bundle args = new Bundle();
        args.putSerializable(ORDER_KEY, orderItem);
        OrderFormFragment fragment = new OrderFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ct_order_form, menu);
        mOptionMenuItem = menu.findItem(R.id.action);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (TextUtils.isEmpty(mActionTitle)) {
            mOptionMenuItem.setVisible(false);
        } else {
            mOptionMenuItem.setVisible(true);
            mOptionMenuItem.setTitle(mActionTitle);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ct_order_form_order, null);
        return view;
    }

    public void renderWithData() {
        if (getArguments().containsKey(ORDER_KEY)) {
            OrderItem orderItem = (OrderItem) getArguments().getSerializable(ORDER_KEY);
            boolean isImTravel = LoginInstance.getInstance(getActivity()).getUserInfo().isTravel();
            mOrderDetailView = isImTravel ? new TravelOrderDetailView() : new FinderOrderDetailView();
            switch (orderItem.getStatus()) {
                case OrderItem.STATUS_WAIT_COFIRM:
                    mOrderDetailView.switchOver();
                    break;
                case OrderItem.STATUS_WAIT_PAY:
                    mOrderDetailView.switchWaitPay();
                    break;
                case OrderItem.STATUS_WAIT_START:
                    mOrderDetailView.switchWaitStart();
                    break;
                case OrderItem.STATUS_WAIT_END:
                    mOrderDetailView.switchWaitEnd();
                    break;
                case OrderItem.STATUS_WAIT_COMMENT:
                    mOrderDetailView.switchWaitComment();
                    break;
                case OrderItem.STATUS_OVER:
                    mOrderDetailView.switchOver();
                    break;
                default:
                    mOrderDetailView.switchUnvaliable();
                    break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public class TravelOrderDetailView implements IOrderDetailView {
        @Override
        public void switchWaitConfirm() {

        }

        @Override
        public void switchWaitPay() {

        }

        @Override
        public void switchWaitStart() {

        }

        @Override
        public void switchWaitEnd() {

        }

        @Override
        public void switchWaitComment() {

        }

        @Override
        public void switchOver() {

        }

        @Override
        public void switchUnvaliable() {

        }
    }

    public class FinderOrderDetailView implements IOrderDetailView {
        @Override
        public void switchWaitConfirm() {

        }

        @Override
        public void switchWaitPay() {

        }

        @Override
        public void switchWaitStart() {

        }

        @Override
        public void switchWaitEnd() {

        }

        @Override
        public void switchWaitComment() {

        }

        @Override
        public void switchOver() {

        }

        @Override
        public void switchUnvaliable() {

        }
    }
}
