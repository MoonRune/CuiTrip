package com.cuitrip.app.orderdetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuitrip.app.CancelOrderActivity;
import com.cuitrip.app.CommentActivity;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.ModifyOrderActivity;
import com.cuitrip.app.pay.PayOrderAcivity;
import com.cuitrip.app.pro.ServicePartRenderData;
import com.cuitrip.app.rong.RongTitleTagHelper;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;

/**
 * Created by baziii on 15/8/11.
 */
public class OrderFormActivity extends BaseActivity {

    public static final String TAG = "OrderFormActivity";
    public static final String ORDER_ID = "OrderFormActivity.ORDER_ID";
    public static final String TARGET_ID = "OrderFormActivity.TARGET_ID";
    @InjectView(R.id.ct_view_pager_indicator)
    IconPageIndicator mViewPagerIndicator;
    @InjectView(R.id.ct_view_pager)
    ViewPager mViewPager;
    @InjectView(R.id.ct_user_name_tv)
    TextView mItsNameTv;
    @InjectView(R.id.ct_top_v)
    View mTopV;
    OrderViewsAdapter mAdapter;
    OrderItem orderItem = null;

    AsyncHttpClient mClient = new AsyncHttpClient();
    boolean showTabs = false;
    protected static final String[] CONTENT = new String[]{"This", "Is", "A",};

    public static void start(Context context, String orderId) {
        Intent intent = new Intent(context, OrderFormActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        context.startActivity(intent);
    }

    public static void start(Context context, String orderId, String targetId) {
        Intent intent = new Intent(context, OrderFormActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(TARGET_ID, targetId);
        context.startActivity(intent);
    }

    public void requestOrderDetail() {
        showLoading();
        renderUi(null);
        LogHelper.e(TAG, "requestOrderDetail  " + getIntent().getStringExtra(ORDER_ID));
        OrderBusiness.getOrderInfo(this, mClient, new LabAsyncHttpResponseHandler(OrderItem.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideLoading();
                LogHelper.e(TAG, "requestOrderDetail suc " + response.result);
                if (data != null) {
                    LogHelper.e(TAG, "requestOrderDetail suc " + (data == null));
                    try {
                        renderUi((OrderItem) data);
                    } catch (Exception e) {
                        LogHelper.e("omg", e.getMessage());
                    }
                    LogHelper.e(TAG, "requestOrderDetail suc finish");
                } else {

                    LogHelper.e(TAG, "requestOrderDetail suc  nulll");
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideLoading();
                LogHelper.e(TAG, "requestOrderDetail failed ");

            }
        }, getIntent().getStringExtra(ORDER_ID));
    }

    public class OrderViewsAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public OrderViewsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getIconResId(int i) {
            switch (i) {
                case 0:
                    return R.drawable.selector_order_info;
                case 1:
                    return R.drawable.selector_order_person;
                default:
                    return R.drawable.selector_order_conversation;
            }

        }

        @Override
        public int getCount() {
            int count = orderItem == null ? 0 : (showTabs ? ((null == canConversationTargetId) ? 2 : 3) : 1);
            return count;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    LogHelper.e("omg", "new instance 0");
                    return orderFormFragment = OrderFormFragment.newInstance(orderItem);
                case 1:
                    String id = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo().getUid();
                    if (id.equals(orderItem.getTravellerId())) {
                        id = orderItem.getInsiderId();
                    } else {
                        id = orderItem.getTravellerId();
                    }
                    return PersonInfoFragment.newInstance(id);
                default:

                    ConversationFragment fragment = new ConversationFragment();
                    if (canConversationTargetId.needBuild()) {
                        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation").
                                appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase()).
                                appendQueryParameter("targetIds", TextUtils.join(",", canConversationTargetId.getUserIds())).
                                appendQueryParameter("delimiter", ",").appendQueryParameter("title", canConversationTargetId.getTitle()).build();
                        fragment.setUri(uri);
                    } else {
                        String target = canConversationTargetId.getTargetId();
                        LogHelper.e("conversation target ", target);
                        String title = orderItem.getServiceName();
                        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                .appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase())
                                .appendQueryParameter("targetId", target).appendQueryParameter("title", title).build();
                        fragment.setUri(uri);
                    }
                    return fragment;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            if (position == 0) {
                orderFormFragment = null;
            }
        }
    }

    OrderFormFragment orderFormFragment;
    ConversationDependy canConversationTargetId;

    public String buildOrderConversationTitle(OrderItem orderItem) {
        return RongTitleTagHelper.buildTitle(orderItem);
    }

    public void validateHasConversation(final OrderItem orderItem) {
        LogHelper.e("omg", "has validateHasConversation  ");
        RongIM.getInstance().getRongIMClient().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(final List<Conversation> conversations) {
                queryForConversationList(orderItem, conversations);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogHelper.e("omg", "validateHasConversation error " + errorCode.getMessage());
            }
        });
    }

    public void queryForConversationList(final OrderItem orderItem, final List<Conversation> conversations) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //getConversationList 无法获取到title  通过 getDiscussion 注射title
                if (conversations == null){
                    return;
                }
                HashMap<String, Conversation> noTitleConversations = new HashMap<String, Conversation>();
                for (Conversation conversation : conversations) {
                    if (TextUtils.isEmpty(conversation.getConversationTitle())) {
                        noTitleConversations.put(conversation.getTargetId(), conversation);
                    }
                }
                final CountDownLatch countDownLatch = new CountDownLatch(noTitleConversations.size());
                for (final String key : noTitleConversations.keySet()) {
                    final Conversation conversation = noTitleConversations.get(key);
                    RongIM.getInstance().getRongIMClient().getDiscussion(key, new RongIMClient.ResultCallback<Discussion>() {
                        public void onSuccess(Discussion discussion) {
                            conversation.setConversationTitle(discussion.getName());
                            countDownLatch.countDown();
                        }


                        public void onError(RongIMClient.ErrorCode errorCode) {
                            countDownLatch.countDown();
                        }
                    });
                }

                //wait api search name end
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // use   conversation which has lasted message
                Conversation lasted = null;
                for (Conversation conversation : conversations) {
                    if (RongTitleTagHelper.isBelongToOrder(conversation.getConversationTitle(), orderItem)) {
                        if (lasted == null) {
                            lasted = conversation;
                        } else {
                            lasted = conversation.getSentTime() > lasted.getSentTime() ? conversation : lasted;
                        }
                    }
                }
                if (lasted != null) {
                    if (!isFinishing()) {
                        final String targetId = lasted.getTargetId();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                canConversationTargetId = new ConversationDependy(targetId);
                                mAdapter.notifyDataSetChanged();
                                mViewPagerIndicator.notifyDataSetChanged();
                            }
                        });
                    }
                }
                //todo  actiivty关闭情况 eventbus
                if (!isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buildConversation(orderItem);
                        }
                    });
                }
            }
        }).start();
        LogHelper.e("omg", "validateHasConversation none ");
    }

    public class ConversationDependy {
        String targetId;

        List<String> userIds;
        String title;

        public ConversationDependy(String targetId) {
            this.targetId = targetId;
        }

        public ConversationDependy(List<String> userIds, String title) {
            this.userIds = userIds;
            this.title = title;
        }

        public String getTargetId() {
            return targetId;
        }

        public List<String> getUserIds() {
            return userIds;
        }

        public String getTitle() {
            return title;
        }

        public boolean needBuild() {
            return TextUtils.isEmpty(targetId);
        }
    }

    public void buildConversation(OrderItem orderItem) {
        List<String> userIds = new ArrayList<>();
        userIds.add(orderItem.getInsiderId());
        userIds.add(orderItem.getTravellerId());
        String title = buildOrderConversationTitle(orderItem);
        canConversationTargetId = new ConversationDependy(userIds, title);
        mAdapter.notifyDataSetChanged();
        mViewPagerIndicator.notifyDataSetChanged();
    }

    public String buildItsname(OrderItem orderItem) {
        return ServicePartRenderData.getStatusText(orderItem);
    }

    public void renderUi(OrderItem orderItem) {
        this.orderItem = orderItem;
        if (orderItem != null) {
            showTabs = orderItem.getStatus() != orderItem.STATUS_WAIT_END;
            mItsNameTv.setText(buildItsname(this.orderItem));
            if (canConversationTargetId == null) {
                validateHasConversation(this.orderItem);
            }
        }
        if (orderItem != null && orderFormFragment != null) {
            orderFormFragment.refresh(orderItem);
        }
        mTopV.setVisibility(showTabs ? View.VISIBLE : View.GONE);
        mAdapter.notifyDataSetChanged();
        mViewPagerIndicator.notifyDataSetChanged();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_order_form);
        showActionBar(R.string.ct_order);
        ButterKnife.inject(this);
        mViewPager.setAdapter(mAdapter = new OrderViewsAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mViewPagerIndicator.setViewPager(mViewPager);
        String targetId = getIntent().getStringExtra(TARGET_ID);
        if (!TextUtils.isEmpty(targetId)) {
            canConversationTargetId = new ConversationDependy(targetId);
        }
        requestOrderDetail();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogHelper.e("omg", "requestCode " + requestCode + "| resultCode " + resultCode);
        if (CancelOrderActivity.isCanceled(requestCode, resultCode, data)) {
            LogHelper.e("omg", "iscanceled");
            requestOrderDetail();
        } else if (PayOrderAcivity.isPaySUc(requestCode, resultCode, data)) {
            LogHelper.e("omg", "isPaySUc");
            requestOrderDetail();
        } else if (ModifyOrderActivity.isModifyed(requestCode, resultCode, data)) {
            LogHelper.e("omg", "isModifyed");
            requestOrderDetail();
        } else if (CommentActivity.isComment(requestCode, resultCode, data)) {
            LogHelper.e("omg", "isComment");
            requestOrderDetail();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
