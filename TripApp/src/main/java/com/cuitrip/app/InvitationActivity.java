package com.cuitrip.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.share.ShareUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 7/16.
 */
public class InvitationActivity extends BaseActivity {

    public static final String TAG = "InvitationActivity";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, InvitationActivity.class);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(getString(R.string.ct_invitation_friends));
        setContentView(R.layout.ct_invitation_friend);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.ct_invitation_friend_tv)
    public void share() {
        ShareUtil.share(this, findViewById(R.id.ct_content_ll), getString(R.string.app_slogan), getString(R.string.app_slogan), "http://www.cuitrip.com/",
               "");

    }

}
