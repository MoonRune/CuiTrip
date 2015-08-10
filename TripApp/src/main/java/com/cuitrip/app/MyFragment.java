package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseFragment;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.loopj.android.http.AsyncHttpClient;


public class MyFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "MyFragment";
    public static final int REQUEST_FOR_EDIT = 1002;

    private View mContentView;
    private View mLogin;
    private ImageView mImage;
    private TextView mName, mDesc;
    private Button mSwitch;

    AsyncHttpClient mClient = new AsyncHttpClient();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.e("omg", "oncreate");
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public void onResume() {
        super.onResume();
        LogHelper.e("omg", "onresume");
        showActionBar(getString(R.string.ct_my));
        setHasOptionsMenu(true);
        if (LoginInstance.isLogin(MainApplication.sContext)) {
            if (mLogin.getVisibility() == View.VISIBLE) {
                requestForHomepageStatus();
            }
            mLogin.setVisibility(View.GONE);
            mContentView.findViewById(R.id.my_content).setVisibility(View.VISIBLE);
            UserInfo info = LoginInstance.getInstance(MainApplication.sContext).getUserInfo();
            ImageHelper.displayPersonImage(info.getHeadPic(), mImage, null);
            mName.setText(info.getNick());
            mDesc.setText(TextUtils.isEmpty(info.getSign()) ? getString(R.string.ct_no_sign) : info.getSign());
            mSwitch.setText(LoginInstance.getInstance(getActivity()).getUserInfo().isTravel()
                    ? getString(R.string.ct_to_finder) : getString(R.string.ct_to_travel));
        } else {
            mLogin.setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.my_content).setVisibility(View.GONE);
            mContentView.findViewById(R.id.ct_login).setOnClickListener(mLoginListener);
        }
    }

    private View.OnClickListener mLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            reLogin();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SelfHomePageEditorActivity.isEdited(requestCode, resultCode, data)) {
            requestForHomepageStatus();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogHelper.e("omg", "onCreateView");
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.ct_my, container, false);
            mContentView.findViewById(R.id.author_profile).setOnClickListener(this);
            mContentView.findViewById(R.id.author_identification).setOnClickListener(this);
            mContentView.findViewById(R.id.author_call_friends).setOnClickListener(this);
            mContentView.findViewById(R.id.ct_help).setOnClickListener(this);
            mSwitch = (Button) mContentView.findViewById(R.id.ct_switch);
            mSwitch.setOnClickListener(this);
            mLogin = mContentView.findViewById(R.id.ct_no_login);
            mImage = (ImageView) mContentView.findViewById(R.id.author_img);
            mName = (TextView) mContentView.findViewById(R.id.author_name);
            mDesc = (TextView) mContentView.findViewById(R.id.author_desc);
        } else {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (LoginInstance.isLogin(MainApplication.sContext)) {
            requestForHomepageStatus();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.author_profile:
                startActivity(new Intent(getActivity(), SelfActivity.class));
                break;
//            case R.id.author_homepage:
//                break;
            case R.id.author_identification:
                startActivity(new Intent(getActivity(), SelfIdentificationActivity.class));
                break;
            case R.id.author_call_friends:
                InvitationActivity.startActivity(getActivity());
                break;
            case R.id.ct_help:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                break;
            case R.id.ct_switch:
                final UserInfo info = LoginInstance.getInstance(getActivity()).getUserInfo();
                final int type = info.isTravel() ? UserInfo.USER_FINDER : UserInfo.USER_TRAVEL;
                showLoading();
                UserBusiness.changeType(getActivity(), new AsyncHttpClient(), new LabAsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        hideLoading();
                        info.setType(type);
                        Intent intent = new Intent(getActivity(), IndexActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        LoginInstance.update(getActivity(), info);
                    }

                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        hideLoading();

                        //TODO:
                    }
                }, type);
                break;
        }
    }

    public void requestForHomepageStatus() {
        (mContentView.findViewById(R.id.ct_home_page_error)).setVisibility(View.GONE);
        (mContentView.findViewById(R.id.ct_homepage_arrow_v)).setVisibility(View.VISIBLE);
        ((TextView) mContentView.findViewById(R.id.ct_home_page_status_tv)).setText(getString(R.string.loading_text));
        mContentView.findViewById(R.id.author_homepage).setOnClickListener(null);
        UserBusiness.getIntroduce(getActivity(), mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                LogHelper.e(TAG, "onsuc");
                try {
                    JSONObject json = JSONObject.parseObject(data.toString());

                    Integer status = json.getInteger("introduceAuditStatus");
                    String reason = json.getString("introduceFailedReason");
                    final String desc = json.getString("introduce");
                    LogHelper.e(TAG, "onsuc :" + status + "|" + reason);
                    if (status == null) {
                        mContentView.findViewById(R.id.author_homepage).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (getActivity() != null) {
                                    SelfHomePageEditorActivity.startActivity(getActivity(), "");
                                }
                            }
                        });
                        ((TextView) mContentView.findViewById(R.id.ct_home_page_status_tv)).setText("");
                        return;
                    }
                    switch (status) {
                        case 0:
                            ((TextView) mContentView.findViewById(R.id.ct_home_page_status_tv)).setText(getString(R.string.ct_homepage_status_ing));
                            (mContentView.findViewById(R.id.ct_home_page_error)).setVisibility(View.GONE);
                            (mContentView.findViewById(R.id.ct_homepage_arrow_v)).setVisibility(View.GONE);
                            break;
                        case 1:
                            ((TextView) mContentView.findViewById(R.id.ct_home_page_status_tv)).setText(getString(R.string.ct_homepage_status_suc));
                            (mContentView.findViewById(R.id.ct_home_page_error)).setVisibility(View.GONE);
                            mContentView.findViewById(R.id.author_homepage).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (getActivity() != null) {
                                        SelfHomePageActivity.startForResult(MyFragment.this, REQUEST_FOR_EDIT);
                                    }
                                }
                            });
                            break;
                        case 2:
                            ((TextView) mContentView.findViewById(R.id.ct_home_page_status_tv)).setText(getString(R.string.ct_homepage_status_failed));
                            if (TextUtils.isEmpty(reason)) {
                                reason = getString(R.string.ct_homepage_status_failed);
                            }
                            (mContentView.findViewById(R.id.ct_home_page_error)).setVisibility(View.VISIBLE);
                            ((TextView) mContentView.findViewById(R.id.ct_home_page_error)).setText(reason);
                            mContentView.findViewById(R.id.author_homepage).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (getActivity() != null) {
                                        SelfHomePageEditorActivity.startActivity(getActivity(),
                                                desc == null ? "" : desc);
                                    }
                                }
                            });
                            break;
                        default:
                            onUnExceptedError();
                            break;
                    }
                } catch (Exception e) {
                    onUnExceptedError();
                }
            }

            public void onUnExceptedError() {
                (mContentView.findViewById(R.id.ct_homepage_arrow_v)).setVisibility(View.GONE);
                ((TextView) mContentView.findViewById(R.id.ct_home_page_status_tv)).setText(getString(R.string.ct_fetch_failed));
                (mContentView.findViewById(R.id.ct_home_page_error)).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                LogHelper.e(TAG, "onfailed");
                String msg = response.msg;
                if (TextUtils.isEmpty(msg)) {
                    msg = getString(R.string.ct_fetch_failed);
                }
                ((TextView) mContentView.findViewById(R.id.ct_home_page_status_tv)).setText(msg);
                ((TextView) mContentView.findViewById(R.id.ct_home_page_error)).setVisibility(View.GONE);
                (mContentView.findViewById(R.id.ct_homepage_arrow_v)).setVisibility(View.GONE);
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ct_menu_my, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
