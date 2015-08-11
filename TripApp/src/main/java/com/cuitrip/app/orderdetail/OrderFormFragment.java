package com.cuitrip.app.orderdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab.app.BaseFragment;

/**
 * Created by baziii on 15/8/11.
 */
public class OrderFormFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
}
