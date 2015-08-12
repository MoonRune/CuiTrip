package com.cuitrip.app.orderdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuitrip.app.pro.OrderPersonRenderData;
import com.cuitrip.app.pro.OrderPersonViewHolder;
import com.cuitrip.service.R;
import com.lab.app.BaseFragment;

/**
 * Created by baziii on 15/8/12.
 */
public class PersonInfoFragment extends BaseFragment {
    OrderPersonViewHolder orderPersonViewHolder = new OrderPersonViewHolder();

    public static final String USER_ID="PersonInfoFragment.USER_ID";
    public static PersonInfoFragment newInstance(String uid) {

        Bundle args = new Bundle();
        args.putString(USER_ID,uid);
        PersonInfoFragment fragment = new PersonInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ct_order_person,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderPersonViewHolder.build(view);
        orderPersonViewHolder.render(OrderPersonRenderData.mock());
    }
}
