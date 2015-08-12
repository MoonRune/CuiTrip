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
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuitrip.app.CancelOrderActivity;
import com.cuitrip.app.CommentActivity;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.ModifyOrderActivity;
import com.cuitrip.app.PayActivity;
import com.cuitrip.app.rong.RongTitleTagHelper;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.viewpagerindicator.IconPageIndicator;
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
    @InjectView(R.id.ct_view_pager_indicator)
    IconPageIndicator mViewPagerIndicator;
    @InjectView(R.id.ct_view_pager)
    ViewPager mViewPager;
    @InjectView(R.id.ct_user_name_tv)
    TextView mItsNameTv;
    OrderViewsAdapter mAdapter;
    OrderItem orderItem = null;
//    {
//        orderItem.setStatus(OrderItem.STATUS_WAIT_COFIRM);
//        orderItem.setOid("oid");
//        orderItem.setBuyerNum("3");
//        orderItem.setServiceName("mocked service ");
//        orderItem.setServiceDate("2015-07-26 22:38:56.00");
//        orderItem.setComment("comment");
//        orderItem.setCommentScore("3");
//        orderItem.setGmtCreated("2015-06-26 22:38:56.00");
//        orderItem.setGmtModified("2015-09-26 22:38:56.00");
//        orderItem.setInsiderId("sdf");
//        orderItem.setHeadPic("http://cdn.aixifan.com/dotnet/artemis/u/cms/www/201508/07094633g8p80txx.jpg");
//        orderItem.setServicePIC("http://static.acfun.mm111.net/dotnet/artemis/u/cms/www/201508/04103955tj3f.jpg");
//        orderItem.setInsiderSign("setInsiderSign");
//        orderItem.setTravellerId("setTravellerId");
//        orderItem.setTravellerName("setTravellerName");
//        orderItem.setInsiderName("setInsiderName");
//        orderItem.setUserNick("setUserNick");
//        orderItem.setPriceType("1");;
//        orderItem.setMoneyType("twd");
//        orderItem.setServicePrice("10");
//        orderItem.setPayCurrency("cny");
//        orderItem.setOrderPrice("2");
//
//    }

    AsyncHttpClient mClient = new AsyncHttpClient();
    boolean showTabs = false;
    protected static final String[] CONTENT = new String[]{"This", "Is", "A",};

    public static void start(Context context, String orderId) {
        Intent intent = new Intent(context, OrderFormActivity.class);
        intent.putExtra(ORDER_ID, orderId);
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
                        orderItem = (OrderItem) data;
                        showTabs = orderItem.getStatus() != orderItem.STATUS_WAIT_END;
                        renderUi(orderItem);
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
            int count = orderItem == null ? 0 : (showTabs ? (TextUtils.isEmpty(canConversationTargetId) ? 2 : 3) : 1);
//            LogHelper.e("omg", "counset " + count + "|" + (orderItem == null) + "|" + showTabs);
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
                    String target = canConversationTargetId;
                    LogHelper.e("conversation target ", canConversationTargetId);
                    String title = orderItem.getServiceName();
        /* 传入私聊会话 PRIVATE 的参数*/
                    Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                            .appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase())
                            .appendQueryParameter("targetId", target).appendQueryParameter("title", title).build();

//rong://com.cuitrip.service/conversation/discussion?targetId=21026d2b-7063-4d5b-bc3a-1d4600e3f935&title=ct_test_for_send_2_1
                    fragment.setUri(uri);
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
    String canConversationTargetId;

    public String buildOrderConversationTitle(OrderItem orderItem) {
        return RongTitleTagHelper.buildTitle(orderItem);
    }

    public void validateHasConversation(final OrderItem orderItem) {
        LogHelper.e("omg", "has validateHasConversation  ");
        RongIM.getInstance().getRongIMClient().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations != null) {
                    for (Conversation conversation : conversations) {
                        if (RongTitleTagHelper.isBelongToOrder(conversation.getConversationTitle(), orderItem)) {
                            canConversationTargetId = conversation.getTargetId();
                            mAdapter.notifyDataSetChanged();
                            mViewPagerIndicator.notifyDataSetChanged();
                            return;
                        }
                    }
                }
                LogHelper.e("omg", "validateHasConversation none ");
                buildConversation(orderItem);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogHelper.e("omg", "validateHasConversation error " + errorCode.getMessage());
            }
        });
    }

    public void buildConversation(OrderItem orderItem) {
        List<String> userIds = new ArrayList<>();
        userIds.add(orderItem.getInsiderId());
        userIds.add(orderItem.getTravellerId());
        final String title = buildOrderConversationTitle(orderItem);
        RongIM.getInstance().getRongIMClient().createDiscussion(title, userIds, new RongIMClient.CreateDiscussionCallback() {
            @Override
            public void onSuccess(String tarid) {
                canConversationTargetId = tarid;
                mAdapter.notifyDataSetChanged();
                mViewPagerIndicator.notifyDataSetChanged();
                LogHelper.e("omg", "buildConversation suc " + tarid + " |" + title);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                LogHelper.e("omg", "buildConversation error " + errorCode.getMessage());
            }
        });
    }

    public String buildItsname(OrderItem orderItem) {
        UserInfo userInfo = LoginInstance.getInstance(this).getUserInfo();
        if (userInfo!=null && !TextUtils.isEmpty(userInfo.getUid()) &&TextUtils.isEmpty(orderItem.getInsiderId()) ) {
            if (userInfo.getUid().equals(orderItem.getExtInfo())) {
                return orderItem.getTravellerName();
            }
            return orderItem.getInsiderName();
        }else{
            return "error";
        }
    }
    public void renderUi(OrderItem orderItem) {
        this.orderItem = orderItem;
        mAdapter.notifyDataSetChanged();
        mViewPagerIndicator.notifyDataSetChanged();
        if (orderItem != null) {
            mItsNameTv.setText(buildItsname(this.orderItem));
            validateHasConversation(this.orderItem);
        }
        if (orderItem != null && orderFormFragment != null) {
            orderFormFragment.refresh(orderItem);
        }

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
        requestOrderDetail();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogHelper.e("omg", "requestCode " + requestCode + "| resultCode " + resultCode);
        if (CancelOrderActivity.isCanceled(requestCode, resultCode, data)) {
            LogHelper.e("omg", "iscanceled");
            requestOrderDetail();
        } else if (PayActivity.isPaySUc(requestCode, resultCode, data)) {
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
