package com.cuitrip.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.imageupload.URLImageParser;
import com.loopj.android.http.AsyncHttpClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created on 7/16.
 */
public class SelfHomePageActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "SelfHomePageActivity";
    @InjectView(R.id.ct_swipe_refresh_layout)
    public SwipeRefreshLayout mSwipRl;
    @InjectView(R.id.ct_content_tv)
    public TextView mContentTv;
    @InjectView(R.id.ct_ava_riv)
    public CircleImageView mAvaRiv;
    @InjectView(R.id.ct_nick_tv)
    public TextView mNickTv;
    @InjectView(R.id.ct_sign_tv)
    public TextView mSignTv;

    AsyncHttpClient mClient = new AsyncHttpClient();

    String introduce;

    public static void startForResult(Fragment fragment,int request) {
        Intent intent = new Intent(fragment.getActivity(),SelfHomePageActivity.class);
        fragment.startActivityForResult(intent,request);

    }
    protected Drawable buildDrawable(Bitmap bitmap) {
        Drawable drawable =
                new BitmapDrawable(getResources(), bitmap);
        int tempWidth = bitmap.getWidth();
        int height = bitmap.getHeight();
        int width = MainApplication.sContext.getPageWidth();
        float leftPadding = getResources().getDimension(R.dimen.ct_personal_desc_left_padding);
        float topPadding = getResources().getDimension(R.dimen.ct_personal_desc_top_padding);
        width -= leftPadding;
        height = width * height / tempWidth;
        drawable.setBounds((int) leftPadding, (int) topPadding, (width > 0 ? width : 0) - (int) leftPadding, (height > 0 ? height : 0) + (int) topPadding);
        return drawable;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_homepage);
        setContentView(R.layout.ct_my_home);
        ButterKnife.inject(this);
        mSwipRl.setOnRefreshListener(this);
        fetchData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_homepage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editor:
                if (!mSwipRl.isRefreshing()) {
                    SelfHomePageEditorActivity.startActivity(this, introduce);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SelfHomePageEditorActivity.REQUEST_HOME_PAGE_UPDATE && resultCode == SelfHomePageEditorActivity.HOME_PAGE_UPDATED) {
            setResult( SelfHomePageEditorActivity.HOME_PAGE_UPDATED);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startRefresh() {
        mSwipRl.setRefreshing(true);
    }

    public void stopRerfresh() {
        mSwipRl.setRefreshing(false);
    }

    public void fetchData() {
        startRefresh();
        UserBusiness.getIntroduce(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                LogHelper.e(TAG, "" + data.toString());
                try {
                    JSONObject json = JSONObject.parseObject(data.toString());

                    Integer status = json.getInteger("introduceAuditStatus");
                    if (status == null) {
                        SelfHomePageEditorActivity.startActivity(SelfHomePageActivity.this, "");
                        finish();
                        return;
                    }
                    if (status != 1) {
                        MessageUtils.showToast(R.string.ct_homepage_status_not_suc);
                        finish();
                        return ;
                    }
                    String nick = json.getString("nick");
                    mNickTv.setVisibility(TextUtils.isEmpty(nick) ? View.GONE : View.VISIBLE);
                    mNickTv.setText(nick);

                    String ava = json.getString("headPic");
                    ImageHelper.displayPersonImage(ava, mAvaRiv, null);

                    String sign = json.getString("sign");
                    mSignTv.setText(TextUtils.isEmpty(sign) ? getString(R.string.ct_no_sign) : sign);

                    introduce = json.getString("introduce");
                    URLImageParser p = new URLImageParser(mContentTv, SelfHomePageActivity.this, introduce);
                    mContentTv.setText(Html.fromHtml(introduce, p, null));
                } catch (Exception e) {
                    LogHelper.e(TAG, "get introduce pic: " + e);
                    MessageUtils.showToast(R.string.ct_fetch_failed);
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                String msg=response.msg;
                if (TextUtils.isEmpty(msg)) {
                    msg=getString(R.string.ct_fetch_failed);
                }
                MessageUtils.showToast(msg);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                stopRerfresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        fetchData();
    }
}
