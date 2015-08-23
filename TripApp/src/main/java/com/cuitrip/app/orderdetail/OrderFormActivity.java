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
import com.cuitrip.app.base.ProgressingFragment;
import com.cuitrip.app.pay.PayOrderAcivity;
import com.cuitrip.app.pro.ServicePartRenderData;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.widget.IconPageIndicator;
import com.loopj.android.http.AsyncHttpClient;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by baziii on 15/8/11.
 */
public class OrderFormActivity extends BaseActivity {

    public static final String TAG = "OrderFormActivity";
    public static final String ORDER_ID = "OrderFormActivity.ORDER_ID";
    public static final String TARGET_ID = "OrderFormActivity.TARGET_ID";
    public static final String MOVE_TO_CONVERSATION = "OrderFormActivity.MOVE_TO_CONVERSATION";
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
    boolean showTabs = true;

    public static void start(Context context, String orderId) {
        Intent intent = new Intent(context, OrderFormActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        context.startActivity(intent);
    }

    public static Intent getStartIntent(Context context, String orderId) {
        Intent intent = new Intent(context, OrderFormActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(MOVE_TO_CONVERSATION, true);
        return intent;
    }

    public static void start(Context context, String orderId, String targetId) {
        Intent intent = new Intent(context, OrderFormActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(TARGET_ID, targetId);
        intent.putExtra(MOVE_TO_CONVERSATION, true);
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
                        if (moveToConversation && mViewPager != null) {
                            mViewPager.setCurrentItem(2);
                            moveToConversation = false;
                        }
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
            int count = orderItem == null ? 0 : (showTabs ? ((orderItem == null || TextUtils.isEmpty(orderItem.getTargetId())) ? 3 : 3) : 1);
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
                    if (!TextUtils.isEmpty(orderItem.getTargetId())) {
                        ConversationFragment fragment = new ConversationFragment();
                        String target = orderItem.getTargetId();
                        LogHelper.e(TAG, "build fragment   target id" + target);
                        String title = orderItem.getServiceName();
                        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                .appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase())
                                .appendQueryParameter("targetId", target).appendQueryParameter("title", title).build();
                        fragment.setUri(uri);
                        return fragment;
                    } else {
                        return emptyFragment = ProgressingFragment.newInstance();
                    }
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

   Fragment
            emptyFragment = null;

    public void replaceFragment() {
        try {
            if (orderItem!=null &&!TextUtils.isEmpty(orderItem.getTargetId()) &&emptyFragment != null) {

                getSupportFragmentManager().beginTransaction().remove(
                        emptyFragment
//                        getFragmentManager().findFragmentByTag("android:switcher:" + R.id.ct_view_pager + ":" + 2)
                ).commit();
                emptyFragment = null;
            }
        } catch (Exception e) {

        }
        mAdapter.notifyDataSetChanged();
        mViewPagerIndicator.notifyDataSetChanged();
    }

    OrderFormFragment orderFormFragment;

    public String buildOrderConversationTitle(OrderItem orderItem) {
        return orderItem.getOid();
    }

    public void buildConversation(final OrderItem orderItem) {
        List<String> userIds = new ArrayList<>();
        userIds.add(orderItem.getInsiderId());
        userIds.add(orderItem.getTravellerId());
        String title = buildOrderConversationTitle(orderItem);
        try {
            RongIM.getInstance().getRongIMClient().createDiscussion(title, userIds, new RongIMClient.CreateDiscussionCallback() {
                @Override
                public void onSuccess(final String s) {
                    LogHelper.e(TAG, "suc build  target id" + s);
                    OrderBusiness.updateOrderConversation(OrderFormActivity.this, mClient, new LabAsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(LabResponse response, Object data) {
                            LogHelper.e(TAG, "update suc   target id" + s);
                            orderItem.setTargetId(s);
                            replaceFragment();
                        }

                        @Override
                        public void onFailure(LabResponse response, Object data) {
                            LogHelper.e(TAG, "update failed   target id" + s);
                            MessageUtils.showToast("创建聊天失败");
                        }
                    }, orderItem.getOid(), s);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogHelper.e(TAG, "build failed   target " + errorCode);
                    hideNoCancelDialog();
                    MessageUtils.showToast("创建聊天失败");
                }
            });
        } catch (Exception e) {
            MessageUtils.showToast("创建聊天失败");
        }
    }

    public String buildItsname(OrderItem orderItem) {
        return ServicePartRenderData.getStatusText(orderItem);
    }

    public void renderUi(OrderItem orderItem) {
        this.orderItem = orderItem;
        if (orderItem != null) {
            mItsNameTv.setText(buildItsname(this.orderItem));
            LogHelper.e(TAG, "order target id" + orderItem);
            if (TextUtils.isEmpty(orderItem.getTargetId())) {
                buildConversation(this.orderItem);
            }
        }
        if (orderItem != null && orderFormFragment != null) {
            orderFormFragment.refresh(orderItem);
        }
        mTopV.setVisibility(showTabs ? View.VISIBLE : View.GONE);
        replaceFragment();

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
        if (getIntent().getBooleanExtra(MOVE_TO_CONVERSATION, false)) {
            notifyedMoveConversation();
        }
        requestOrderDetail();

    }

    boolean moveToConversation = false;

    public void notifyedMoveConversation() {
        moveToConversation = true;
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
