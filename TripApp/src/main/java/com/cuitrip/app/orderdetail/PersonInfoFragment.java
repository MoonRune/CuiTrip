package com.cuitrip.app.orderdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuitrip.app.pro.OrderPersonRenderData;
import com.cuitrip.app.pro.OrderPersonViewHolder;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseFragment;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by baziii on 15/8/12.
 */
public class PersonInfoFragment extends BaseFragment {
    OrderPersonViewHolder orderPersonViewHolder = new OrderPersonViewHolder();

    public static final String USER_ID = "PersonInfoFragment.USER_ID";
    AsyncHttpClient mClient = new AsyncHttpClient();
    public String uid;

    boolean isFetching = false;
    UserInfo userinfo;

    public static PersonInfoFragment newInstance(String uid) {

        Bundle args = new Bundle();
        args.putString(USER_ID, uid);
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
        View view = inflater.inflate(R.layout.ct_order_person, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderPersonViewHolder.build(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        tryRequest();
    }

    public void tryRequest() {
        if (!isFetching && userinfo == null) {
            requestForUserInfo();
        }
    }

    public void requestForUserInfo() {
        uid = getArguments().getString(USER_ID);
//        showLoading();
        isFetching = true;
        UserBusiness.getUserInfo(getActivity(), mClient, new LabAsyncHttpResponseHandler(UserInfo.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null && data instanceof UserInfo) {
                    orderPersonViewHolder.render(OrderPersonRenderData.getInstance(userinfo = ((UserInfo) data)));
                }
                isFetching = false;
//                hideLoading();
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                MessageUtils.showToast(R.string.data_error);
                isFetching = false;
//                hideLoading();
            }
        }, uid);
    }
}
