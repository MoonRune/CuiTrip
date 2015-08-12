package com.cuitrip.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.cuitrip.app.conversation.ConversationListFragment;
import com.cuitrip.finder.fragment.ServiceFragment;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.cuitrip.push.MessagePrefs;
import com.cuitrip.push.PushService;
import com.cuitrip.service.R;
import com.lab.app.BaseTabHostActivity;
import com.lab.utils.MessageUtils;

public class IndexActivity extends BaseTabHostActivity {

    public static final String GO_TO_TAB = "go_to_tab";

    public static final String RECOMMEND_TAB = "recommend";
    public static final String SERVICE_TAB = "service";
    public static final String MESSAGE_TAB = "message";
    public static final String ORDER_TAB = "order";
    public static final String MY_TAB = "my";

    private View mNewMessageDot;
    private NewMessageComingListener mMessageComingListener;

    private volatile boolean mExiting = false;
    private static final int EXIT_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            String tabTag = intent.getStringExtra(GO_TO_TAB);
            if (!TextUtils.isEmpty(tabTag)) {
                mTabHost.setCurrentTabByTag(tabTag);
            }
        }
        IntentFilter filter = new IntentFilter(PushService.NEW_MESSAGE_BROADCAT);
        registerReceiver(mNewMessageComing, filter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null){
            String tabTag = intent.getStringExtra(GO_TO_TAB);
            if (!TextUtils.isEmpty(tabTag)) {
                mTabHost.setCurrentTabByTag(tabTag);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MessagePrefs.hasNewMessage() && mNewMessageDot != null) {
            mNewMessageDot.setVisibility(View.VISIBLE);
        } else {
            mNewMessageDot.setVisibility(View.GONE);
        }
    }

    public void testClickMy(){
        tab.performClick();
    }
    View tab;
    @Override
    protected void initTabs() {
        UserInfo info = LoginInstance.getInstance(this).getUserInfo();
        if(info != null && !info.isTravel()){
            mTabHost.addTab(mTabHost.newTabSpec(SERVICE_TAB)
                            .setIndicator(createTabView(R.drawable.ct_finder,
                                    getString(R.string.ct_finder))), ServiceFragment.class,
                    null);
        }else{
            mTabHost.addTab(mTabHost.newTabSpec(RECOMMEND_TAB)
                            .setIndicator(createTabView(R.drawable.ct_recommend,
                                    getString(R.string.ct_recommend))), RecommendFragment.class,
                    null);
        }

        mTabHost.addTab(mTabHost.newTabSpec(MESSAGE_TAB)
                .setIndicator(createMeeageTabView(R.drawable.ct_mennsage, getString(R.string.ct_message))),
                ConversationListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(ORDER_TAB)
                .setIndicator(createTabView(R.drawable.ct_order, getString(R.string.ct_order))),
                OrderFragment.class, null);
        mTabHost.addTab( mTabHost.newTabSpec(MY_TAB)
                        .setIndicator(tab =createTabView(R.drawable.ct_my, getString(R.string.ct_my))),
                MyFragment.class, null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (MESSAGE_TAB.equals(s) && mNewMessageDot != null) {
                    mNewMessageDot.setVisibility(View.GONE);
                    MessagePrefs.setHasNewMessage(false);
                }
            }
        });
    }

    private View createTabView(int drawable, String text) {
        View parent = View.inflate(this, R.layout.ct_tab_widget, null);
        ((ImageView)parent.findViewById(R.id.tabIcon)).setImageResource(drawable);
        parent.findViewById(R.id.tab_new).setVisibility(View.GONE);
        return parent;
    }

    private View createMeeageTabView(int drawable, String text) {
        View parent = View.inflate(this, R.layout.ct_tab_widget, null);
        ((ImageView)parent.findViewById(R.id.tabIcon)).setImageResource(drawable);
        mNewMessageDot = parent.findViewById(R.id.tab_new);
        mNewMessageDot.setVisibility(View.GONE);
        return parent;
    }

    @Override
    public void onBackPressed() {
        if(!mExiting){
            mExiting = true;
            MessageUtils.showToast(R.string.ct_exit_tip);
            mTabHost.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExiting = false;
                }
            }, EXIT_TIME);
        }else{
            finish();
        }
    }

    public void setNewMessageComingListener(NewMessageComingListener messageComingListener){
        mMessageComingListener = messageComingListener;
    }

    public interface NewMessageComingListener{
        void onNewMessage();
    }


    //新的push广播消息
    private BroadcastReceiver mNewMessageComing = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && PushService.NEW_MESSAGE_BROADCAT.equals(intent.getAction())){
                if(!MESSAGE_TAB.equals(mTabHost.getCurrentTabTag())){
                    mNewMessageDot.setVisibility(View.VISIBLE);
                }
                if(mMessageComingListener != null){
                    mMessageComingListener.onNewMessage();
                }
            }
        }
    };

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mNewMessageComing);
    }
}
