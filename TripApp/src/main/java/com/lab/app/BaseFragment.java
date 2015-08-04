package com.lab.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;

import com.cuitrip.business.BusinessHelper;
import com.cuitrip.login.LoginActivity;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.service.R;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;

public class BaseFragment extends Fragment {
    private static final int REQUEST_LOGIN = 10001;

    private Dialog mLoadingDialog;
    private Dialog mNoCancelDialog;

    public void showActionBar(String title) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ActionBar actionbar = ((BaseActivity) getActivity()).getSupportActionBar();
            if (actionbar != null) {
                actionbar.setTitle(title);
                actionbar.show();
            }
        }
    }

    public void hideActionBar() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ActionBar actionbar = ((BaseActivity) getActivity()).getSupportActionBar();
            if (actionbar != null) {
                actionbar.hide();
            }
        }
    }

    public ActionBar getActionBar() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).getSupportActionBar();
        } else {
            return null;
        }
    }

    protected void showLoading() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = MessageUtils.getProgressDialog(activity, R.string.loading_text);
        }
        mLoadingDialog.show();
    }

    protected void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    protected void showNoCancelDialog() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (mNoCancelDialog == null) {
            mNoCancelDialog = MessageUtils.getNoCancelProgressDialog(activity, getString(R.string.ct_waiting));
        }
        mNoCancelDialog.show();
    }

    protected void hideNoCancelDialog() {
        if (mNoCancelDialog != null) {
            mNoCancelDialog.dismiss();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mNoCancelDialog != null && mNoCancelDialog.isShowing()) {
            mNoCancelDialog.dismiss();
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected void onNetwokError(int nameRes, int drawableRes, int layout) {

    }

    protected void onEmpyData(int nameRes, int drawableRes, int layout) {

    }

    protected boolean isTokenInvalid(LabResponse response) {
        if (BusinessHelper.isTokenInvalid(response)) {
            reLogin();
            if (!TextUtils.isEmpty(response.msg)) {
                MessageUtils.showToast(response.msg);
            }
            return true;
        }
        return false;
    }

    protected void reLogin() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        startActivityForResult(new Intent(activity, LoginActivity.class), REQUEST_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == Activity.RESULT_OK && LoginInstance.isLogin(getActivity())) {
                onLoginSuccess();
                return;
            } else {
                onLoginFailed();
            }
        }
    }

    protected void onLoginSuccess() {

    }

    protected void onLoginFailed() {

    }
}
