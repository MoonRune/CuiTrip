package com.cuitrip.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.cuitrip.app.conversation.ConversationListFragment;
import com.cuitrip.app.rong.RongCloudEvent;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.finder.fragment.ServiceFragment;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.ForceUpdate;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseTabHostActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.umeng.update.UmengUpdateAgent;

import java.util.concurrent.TimeUnit;

import jonathanfinerty.once.Once;

public class IndexActivity extends BaseTabHostActivity {

    public static final String GO_TO_TAB = "go_to_tab";

    public static final String RECOMMEND_TAB = "recommend";
    public static final String SERVICE_TAB = "service";
    public static final String MESSAGE_TAB = "message";
    public static final String ORDER_TAB = "order";
    public static final String MY_TAB = "my";

    private volatile boolean mExiting = false;
    private static final int EXIT_TIME = 2000;

    @Override
    public void showActionBar(String title) {
        super.showActionBar(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void showActionBar(int titleId) {
        super.showActionBar(titleId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UmengUpdateAgent.update(this);
        Intent intent = getIntent();

        LogHelper.e("LoginActivity", "index oncreate");
        if (intent != null) {
            String tabTag = intent.getStringExtra(GO_TO_TAB);
            LogHelper.e("showNotification", "index ty" + tabTag);
            if (!TextUtils.isEmpty(tabTag)) {
                mTabHost.setCurrentTabByTag(tabTag);
            }
        }
        if (MainApplication.getInstance().validateRong()) {
            RongCloudEvent.ConnectRong(false);
        }
        validateForceUpdate();
    }

    protected void validateForceUpdate() {
        if (!Once.beenDone(TimeUnit.DAYS, 1, MainApplication.DAILY_FORCE_UPDATE)) {
            UserBusiness.forceUpdate(this, new AsyncHttpClient(), new LabAsyncHttpResponseHandler(ForceUpdate.class) {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    if (data != null && data instanceof ForceUpdate) {
                        ForceUpdate update = ((ForceUpdate) data);
                        if (update.isNeedUpdate()) {
                            showForceUpdate(update.getUrl());
                        } else {
                            Once.markDone(MainApplication.DAILY_FORCE_UPDATE);
                        }
                    }
                }

                @Override
                public void onFailure(LabResponse response, Object data) {

                }
            });
        }
    }

    private void showForceUpdate(final String url) {
        View view  =  LayoutInflater.from(this).inflate(R.layout.force_update, null);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(it);
            }
        });
        MessageUtils.createBuilder(this).setCancelable(false).setView(
               view).show().show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String tabTag = intent.getStringExtra(GO_TO_TAB);
            if (!TextUtils.isEmpty(tabTag)) {
                mTabHost.setCurrentTabByTag(tabTag);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    View tab;

    public String firstTag;

    @Override
    protected void initTabs() {
        UserInfo info = LoginInstance.getInstance(this).getUserInfo();
        if (info != null && !info.isTravel()) {
            firstTag = SERVICE_TAB;
            mTabHost.addTab(mTabHost.newTabSpec(SERVICE_TAB)
                            .setIndicator(createTabView(R.drawable.ct_finder,
                                    getString(R.string.ct_finder))), ServiceFragment.class,
                    null);
        } else {
            firstTag = RECOMMEND_TAB;
            mTabHost.addTab(mTabHost.newTabSpec(RECOMMEND_TAB)
                            .setIndicator(createTabView(R.drawable.ct_finder,
                                    getString(R.string.ct_recommend))), RecommendFragment.class,
                    null);
        }
        mTabHost.addTab(mTabHost.newTabSpec(ORDER_TAB)
                        .setIndicator(createTabView(R.drawable.ct_order, getString(R.string.ct_order))),
                OrderFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec(MESSAGE_TAB)
                        .setIndicator(createMeeageTabView(R.drawable.ct_mennsage, getString(R.string.ct_message))),
                ConversationListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(MY_TAB)
                        .setIndicator(tab = createTabView(R.drawable.ct_my, getString(R.string.ct_my))),
                MyFragment.class, null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                lastTabId = CurrentId;
                CurrentId = tabId;
            }
        });
    }

    String lastTabId;
    String CurrentId;

    private View createTabView(int drawable, String text) {
        View parent = View.inflate(this, R.layout.ct_tab_widget, null);
        ((ImageView) parent.findViewById(R.id.tabIcon)).setImageResource(drawable);
        ((TextView) parent.findViewById(R.id.tab_tv)).setText(text);
        return parent;
    }

    private View createMeeageTabView(int drawable, String text) {
        View parent = View.inflate(this, R.layout.ct_tab_widget, null);
        ((ImageView) parent.findViewById(R.id.tabIcon)).setImageResource(drawable);
        ((TextView) parent.findViewById(R.id.tab_tv)).setText(text);
        return parent;
    }

    @Override
    public void onBackPressed() {
        if (!mExiting) {
            mExiting = true;
            MessageUtils.showToast(R.string.ct_exit_tip);
            mTabHost.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExiting = false;
                }
            }, EXIT_TIME);
        } else {
            finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
