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
import com.cuitrip.app.OldConversatoinFragment;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.app.base.ProgressingFragment;
import com.cuitrip.app.conversation.CtConversationFragment;
import com.cuitrip.app.pay.PayOrderAcivity;
import com.cuitrip.app.pro.ServicePartRenderData;
import com.cuitrip.app.rong.RongCloudEvent;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;
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
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;

/**
 * Created by baziii on 15/8/11.
 */
public class OrderFormActivity extends BaseActivity {

    public static String CURRENT_TARGET;
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
    String orderId;
    AsyncHttpClient mClient = new AsyncHttpClient();
    boolean showTabs = true;

    public static void start(Context context, String orderId) {
        Intent intent = new Intent(context, OrderFormActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        context.startActivity(intent);
    }
    public static Intent getStartOrderIntent(Context context, String orderId) {
        Intent intent = new Intent(context, OrderFormActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        return intent;
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
        }, orderId);
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
        public int getItemPosition(Object object) {
            LogHelper.e("replaceFragment", "getItemPosition  " + object + "  " + needRefreshFragmenet);
            if (needRefreshFragmenet && object instanceof ProgressingFragment) {
                needRefreshFragmenet = false;
                return POSITION_NONE;//返回这个表示该对象已改变,需要刷新
            } else {
                return POSITION_UNCHANGED;//反之不刷新
            }
        }

        @Override
        public int getCount() {
            int count = orderItem == null ? 0 : 3;
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
                    LogHelper.e("replaceFragment", "getfragment");
                    if (orderItem.enableRongConversation() || ! orderItem.isOldConversations()) {
                        if (!TextUtils.isEmpty(orderItem.getTargetId())) {

                            UserInfo info = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
                            if (info != null) {
                                RongCloudEvent.onConversationStart(orderId,
                                        orderItem.getOtherId(info.getUid()),
                                        info.getUid(),
                                        orderItem.getSid(),
                                        orderItem.getTargetId());

                            }


                            LogHelper.e("replaceFragment", " do replace");
                            CURRENT_TARGET = orderItem.getTargetId();
                            LogHelper.e("omg member", " CURRENT_TARGET " + CURRENT_TARGET);
                            RongIM.getInstance().getRongIMClient().getDiscussion(CURRENT_TARGET, new RongIMClient.ResultCallback<Discussion>() {
                                @Override
                                public void onSuccess(Discussion discussion) {
                                    LogHelper.e("omg member", TextUtils.join("|", discussion.getMemberIdList()));
                                    try {
                                        if (discussion.getMemberIdList() == null || discussion.getMemberIdList().size() < 2
                                                || !discussion.getMemberIdList().contains(LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo().getUid())
                                                ) {
                                            MainApplication.getInstance().orderRongMembersizeError();
                                            buildConversation(orderItem);
                                        }
                                    } catch (Exception e) {
                                        LogHelper.e(TAG, "failed");
                                    }
                                    LogHelper.e(TAG, "SUC " + discussion.getMemberIdList() + " : " + TextUtils.join("|", discussion.getMemberIdList()));
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    LogHelper.e(TAG, " member failed " + errorCode.name() + "|" + errorCode.getMessage());
                                    if (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION)) {
                                        MainApplication.getInstance().orderRongMembersizeError();
                                        buildConversation(orderItem);
                                    }
                                }
                            });
                            CtConversationFragment fragment = CtConversationFragment.newInstance(orderId,
                                    orderItem.isOldConversations(),
                                    orderItem.getOtherId(info.getUid()));
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
                    } else {
                        UserInfo info = LoginInstance.getInstance(MainApplication.getInstance()).getUserInfo();
                        if (info != null) {
                            return OldConversatoinFragment.newInstance(orderId,orderItem.getOtherId(info.getUid()));
                        }
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

    Fragment emptyFragment = null;
    boolean needRefreshFragmenet = false;

    public void replaceFragment() {
        LogHelper.e("replaceFragment", "replaceFragment");
        try {
            if (orderItem == null){

                LogHelper.e("replaceFragment", "orderItem == null");
            }else {

                LogHelper.e("replaceFragment", orderItem.getTargetId()+"|"+(emptyFragment != null));
            }
            if (orderItem != null && !TextUtils.isEmpty(orderItem.getTargetId()) && emptyFragment != null) {
                needRefreshFragmenet = true;
                LogHelper.e("replaceFragment", " remove ");
                getSupportFragmentManager().beginTransaction().remove(
                        emptyFragment
//                        getFragmentManager().findFragmentByTag("android:switcher:" + R.id.ct_view_pager + ":" + 2)
                ).commit();
                emptyFragment = null;
            }
        } catch (Exception e) {
            LogHelper.e("replaceFragment", "replaceFragment error"+e.getMessage());

        }
        mAdapter.notifyDataSetChanged();
        mViewPagerIndicator.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CURRENT_TARGET = null;
    }

    OrderFormFragment orderFormFragment;

    public String buildOrderConversationTitle(OrderItem orderItem) {
        return orderItem.getOid();
    }

    public void buildConversation(final OrderItem orderItem) {
        List<String> userIds = new ArrayList<>();
        userIds.add(orderItem.getInsiderId());
        userIds.add(orderItem.getTravellerId());
        LogHelper.e("omg", "buildConversation start " + orderItem.getTravellerId() + "  -" + orderItem.getInsiderId());
        if (orderItem.getInsiderId() == null || TextUtils.isEmpty(orderItem.getInsiderId())
                || orderItem.getTravellerId() == null || TextUtils.isEmpty(orderItem.getTravellerId())
                || orderItem.getTravellerId().equals(orderItem.getInsiderId())) {
            MainApplication.getInstance().orderMemberIdError();
            MessageUtils.showToast(R.string.data_error);
            finish();
            return;
        }
        String title = buildOrderConversationTitle(orderItem);
        try {
            LogHelper.e(TAG, "startConversation ");
            RongCloudEvent.getInstance().startConversation(title, userIds, orderId, new CtFetchCallback<String>() {
                @Override
                public void onSuc(final String s) {
                    LogHelper.e(TAG, "startConversation suc build  target id" + s);
                    orderItem.setTargetId(s);
                    replaceFragment();

                }

                @Override
                public void onFailed(CtException throwable) {
                    LogHelper.e(TAG, "startConversation failed build" );
                    MessageUtils.showToast(throwable.getMessage());
                }
            });
        } catch (Exception e) {
            MessageUtils.showToast(R.string.load_error);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogHelper.e("omg", "onNewIntent");
        requestData(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_order_form);
        showActionBar(R.string.ct_order);
        ButterKnife.inject(this);
        mViewPager.setAdapter(mAdapter = new OrderViewsAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        showActionBar(getString(R.string.order_form_order_title));
                        break;
                    case 1:
                        showActionBar(getString(R.string.order_form_user_title));
                        break;
                    case 2:
                        showActionBar(getString(R.string.order_form_conversation_title));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPagerIndicator.setViewPager(mViewPager);
        requestData(getIntent());
        RongCloudEvent.ConnectRong(false);

    }

    public void requestData(Intent intent) {
        if (intent.getBooleanExtra(MOVE_TO_CONVERSATION, false)) {
            notifyedMoveConversation();
        }
        orderId = intent.getStringExtra(ORDER_ID);
        LogHelper.e(TAG, " request " + orderId);
        mViewPager.setCurrentItem(0);
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
