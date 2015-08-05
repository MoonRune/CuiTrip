package com.cuitrip.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created on 7/18.
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_MODIFY = 100;
    private static final int REQUEST_CANCEL = 101;
    private static final int REQUEST_COMMENT = 102;

    public static final String ORDER_ID = "order_id";
    public static final String ORDER_INFO = "order_info";

    private String mOrderId;
    private OrderItem mOrderInfo;
    private MenuItem mCancel;
    private MenuItem mContactCuibin;
    private Button mStatus;
    private Button mContactFinder;
    private UserInfo mUserInfo;

    private AsyncHttpClient mClient = new AsyncHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_order);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mOrderId = intent.getStringExtra(ORDER_ID);
        if (mOrderId == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        getOrderInfo();
    }

    protected void onDestroy() {
        super.onDestroy();
        mClient.cancelAllRequests(true);
    }

    private void getOrderInfo() {
        showLoading();
        LogHelper.e("order id","id "+mOrderId);
        OrderBusiness.getOrderInfo(this, mClient, mResponseHandler, mOrderId);
    }

    protected void onLoginSuccess() {
        getOrderInfo();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_order_detail, menu);
        mCancel = menu.findItem(R.id.action_cancel);
        mContactCuibin = menu.findItem(R.id.action_contact_cuibin);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact_cuibin:
                startActivity(new Intent(this, RelationActivity.class));
                return true;
            case R.id.action_cancel:
                cancelOrder();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelOrder() {
        startActivityForResult(new Intent(this, CancelOrderActivity.class)
                .putExtra(ORDER_INFO, mOrderInfo), REQUEST_CANCEL);
    }

    protected void onLoginFailed() {
        finish();
    }

    private void initViewIng(OrderItem info) {
        setContentView(R.layout.ct_order_detail_ing);
        mContactCuibin.setVisible(true);
        mCancel.setVisible(false);
        ImageHelper.displayCtImage(info.getServicePIC(), (ImageView) findViewById(R.id.order_img), null);
        setViewText(R.id.service_name, info.getServiceName());
        setViewText(R.id.service_address, info.getServiceAddress());

        //TODO 图片替换
        mUserInfo = LoginInstance.getInstance(this).getUserInfo();
        if (mUserInfo != null && mUserInfo.isTravel()) {
            findViewById(R.id.status_button).setVisibility(View.GONE);

            setViewText(R.id.finder_name, info.getUserNick());
            ImageHelper.displayPersonImage(info.getHeadPic(), (ImageView) findViewById(R.id.finder_img), null);
            setViewText(R.id.travel_name, getString(R.string.ct_order_me));
            ImageHelper.displayPersonImage(mUserInfo.getHeadPic(), (ImageView) findViewById(R.id.travel_img), null);
        } else {
            setViewText(R.id.finder_name, getString(R.string.ct_order_me));
            ImageHelper.displayPersonImage(mUserInfo.getHeadPic(), (ImageView) findViewById(R.id.finder_img), null);
            setViewText(R.id.travel_name, info.getUserNick());
            ImageHelper.displayPersonImage(info.getHeadPic(), (ImageView) findViewById(R.id.travel_img), null);
            mStatus = (Button) findViewById(R.id.status_button);
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText(R.string.ct_order_status_close);
            mStatus.setOnClickListener(this);
        }
        String serviceDate = info.getServiceDate();
        int index = serviceDate.indexOf(" ");
        if(index > 4){
            setViewText(R.id.travel_time, serviceDate.substring(0, index));
        }else{
            setViewText(R.id.travel_time, serviceDate);
        }
    }

    private void initView(OrderItem info) {
        setContentView(R.layout.ct_order_detail);
        ImageHelper.displayCtImage(info.getServicePIC(), (ImageView) findViewById(R.id.order_img), null);
        ImageHelper.displayPersonImage(info.getHeadPic(), (ImageView) findViewById(R.id.author_img), null);

        mUserInfo = LoginInstance.getInstance(this).getUserInfo();
//        if (mUserInfo.isTravel()) {
            setViewText(R.id.cuthor_name, info.getUserNick());
//        } else {
//            setViewText(R.id.cuthor_name, info.getTravellerName());
//        }
        setViewText(R.id.service_name, info.getServiceName());
        setViewText(R.id.service_address, info.getServiceAddress());
        String time = info.getServiceDate();
        if (time != null && time.indexOf(" ") > 1) {
            setViewText(R.id.order_date_value, time.substring(0, time.indexOf(" ")));
        } else {
            setViewText(R.id.order_date_value, info.getServiceDate());
        }
        setViewText(R.id.order_persons_value, info.getBuyerNum());
//        setViewText(R.id.order_bill_value, info.getMoneyType() + " " + info.getOrderPrice());
        setViewText(R.id.order_bill_value, info.getPayCurrency() + " " + info.getOrderPrice());

        mContactFinder = (Button) findViewById(R.id.order_contact);
        mContactFinder.setOnClickListener(this);

        if (mUserInfo.isTravel()) {
            initTravelStatus(info);
            if (!TextUtils.isEmpty(info.getComment())) {
                findViewById(R.id.comment_block).setVisibility(View.VISIBLE);
                setViewText(R.id.comment_person, getString(R.string.ct_my_comment));
                setViewText(R.id.comment_content, info.getComment());
            }
            if (info.getCommentScore() > 0) {
                findViewById(R.id.comment_block).setVisibility(View.VISIBLE);
                setViewText(R.id.comment_person, getString(R.string.ct_my_comment));
                ((RatingBar) findViewById(R.id.service_score)).setRating(info.getCommentScore());
            }
        } else {
            initFinderStatus(info);
            if (!TextUtils.isEmpty(info.getComment())) {
                findViewById(R.id.comment_block).setVisibility(View.VISIBLE);
                setViewText(R.id.comment_person,  getString(R.string.ct_someones_comment,info.getUserNick() ));
                setViewText(R.id.comment_content, info.getComment());
            }
            if (info.getCommentScore() > 0) {
                findViewById(R.id.comment_block).setVisibility(View.VISIBLE);
                setViewText(R.id.comment_person,  getString(R.string.ct_someones_comment,info.getUserNick() ));
                ((RatingBar) findViewById(R.id.service_score)).setRating(info.getCommentScore());
            }
        }
    }

    private LabAsyncHttpResponseHandler mResponseHandler = new LabAsyncHttpResponseHandler(OrderItem.class) {
        @Override
        public void onSuccess(LabResponse response, Object data) {
            hideLoading();
            if (data != null) {
                mOrderInfo = (OrderItem) data;
                if (mOrderInfo.getStatus() == 4) {//进行中
                    initViewIng(mOrderInfo);
                } else {
                    initView(mOrderInfo);
                }
            }
        }

        @Override
        public void onFailure(LabResponse response, Object data) {
            hideLoading();
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                } else {
                    MessageUtils.showToast(R.string.ct_fetch_failed);
                }
                return;
        }
    };

    @Override
    public void onClick(View view) {
        if (mUserInfo.isTravel()) {
            onTravelClick(view);
        } else {
            onFinderClick(view);
        }

    }

    private void initTravelStatus(OrderItem info) {
        mStatus = (Button) findViewById(R.id.status_button);
        mStatus.setEnabled(!info.isClosed());
        mStatus.setVisibility(View.VISIBLE);
        mCancel.setVisible(false);
        mContactCuibin.setVisible(false);
        mContactFinder.setVisibility(View.GONE);
        switch (info.getStatus()) {
            case 1: //待确认,可修改预约
                mCancel.setVisible(true);
                mStatus.setText(R.string.ct_order_status_modify);
                mStatus.setOnClickListener(this);
                mContactFinder.setVisibility(View.VISIBLE);
                break;
            case 2: //待支付
                mCancel.setVisible(true);
                mStatus.setText(R.string.ct_order_status_pay);
                mStatus.setOnClickListener(this);
                mContactFinder.setVisibility(View.VISIBLE);
                break;
            case 3: //待开始
                mCancel.setVisible(true);
                mStatus.setText(info.getStatusContent());
                mStatus.setClickable(false);
                mStatus.setEnabled(false);
                mContactFinder.setVisibility(View.VISIBLE);
                break;
            case 4: //进行中
                break;
            case 5: //待评价
                mStatus.setText(R.string.ct_order_status_comment);
                mStatus.setOnClickListener(this);
                mCancel.setVisible(false);
                break;
            case 6: //已完成
                mStatus.setText(info.getStatusContent());
                mStatus.setEnabled(false);
                mCancel.setVisible(false);
                break;
            case 7: //已失效
                mStatus.setText(info.getStatusContent());
                mContactCuibin.setVisible(true);
                mStatus.setEnabled(false);
                mCancel.setVisible(false);
                break;
            default:
                mStatus.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void onTravelClick(View view) {
        switch (view.getId()) {
            case R.id.order_contact:
                startActivity(new Intent(this, MessageDetailActivity.class).putExtra(ORDER_ID, mOrderId));
                break;
            case R.id.status_button:
                if (mOrderInfo == null) {
                    return;
                }
                int status = mOrderInfo.getStatus();
                switch (status) {
                    case 1: //待确认,可修改预约
                        startActivityForResult(new Intent(this, ModifyOrderActivity.class)
                                .putExtra(ORDER_INFO, mOrderInfo), REQUEST_MODIFY);
                        break;
                    case 2: //待支付
//                        startActivityForResult(new Intent(this, CommentActivity.class)
//                                .putExtra(ORDER_ID, mOrderId), REQUEST_COMMENT);
                        PayActivity.startActivity(this,mOrderId);
                        break;
                    case 3: //待开始
                        break;
                    case 4: //进行中
                        break;
                    case 5: //待评价
                        startActivityForResult(new Intent(this, CommentActivity.class)
                                .putExtra(ORDER_INFO, mOrderInfo), REQUEST_COMMENT);
                        break;
                    case 6: //已完成
                        break;
                    case 7: //已失效
                        break;
                }

                break;
        }
    }

    private void initFinderStatus(OrderItem info) {
        mStatus = (Button) findViewById(R.id.status_button);
        mStatus.setEnabled(!info.isClosed());
        mCancel.setVisible(false);
        mStatus.setVisibility(View.VISIBLE);
        mContactCuibin.setVisible(false);
        mContactFinder.setVisibility(View.GONE);
        switch (info.getStatus()) {
            case 1: //待确认
                mCancel.setVisible(true);
                mStatus.setText(R.string.ct_order_status_confirm);
                mStatus.setOnClickListener(this);
                mContactFinder.setVisibility(View.VISIBLE);
                break;
            case 2: //待支付
                mCancel.setVisible(true);
                mStatus.setText(info.getStatusContent());
                mStatus.setClickable(false);
                mStatus.setEnabled(false);
                mContactFinder.setVisibility(View.VISIBLE);
                break;
            case 3: //待开始
                mCancel.setVisible(true);
                mStatus.setText(R.string.ct_order_status_start);
                mStatus.setOnClickListener(this);
                mStatus.setEnabled(false);
                mContactFinder.setVisibility(View.VISIBLE);
                break;
            case 4: //进行中
                break;
            case 5: //待评价
                mStatus.setText(info.getStatusContent());
                mStatus.setEnabled(false);
                mCancel.setVisible(false);
                break;
            case 6: //已完成
                mStatus.setText(info.getStatusContent());
                mStatus.setEnabled(false);
                mCancel.setVisible(false);
                break;
            case 7: //已失效
                mStatus.setText(info.getStatusContent());
                mStatus.setEnabled(false);
                mCancel.setVisible(false);
                mContactCuibin.setVisible(true);
                break;
            default:
                mStatus.setVisibility(View.INVISIBLE);
                mCancel.setVisible(false);
                break;
        }
    }

    private void onFinderClick(View view) {
        switch (view.getId()) {
            case R.id.order_contact:
                startActivity(new Intent(this, MessageDetailActivity.class).putExtra(ORDER_ID, mOrderId));
                break;
            case R.id.status_button:
                if (mOrderInfo == null) {
                    return;
                }
                int status = mOrderInfo.getStatus();
                switch (status) {
                    case 1: //待确认
                        confirmOrder();
                        break;
                    case 2: //待支付
                        break;
                    case 3: //待开始 开始旅程
                        beginOrder();
                        break;
                    case 4: //进行中
                        endOrder();
                        break;
                    case 5: //待评价
                        startActivityForResult(new Intent(this, CommentActivity.class)
                                .putExtra(ORDER_INFO, mOrderInfo), REQUEST_COMMENT);
                        break;
                    case 6: //已完成
                        break;
                    case 7: //已失效
                        break;
                }

                break;
        }
    }

    private void confirmOrder() {
        MessageUtils.createHoloBuilder(this).setMessage(R.string.ct_order_confirm)
                .setPositiveButton(R.string.ct_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showNoCancelDialog();
                        OrderBusiness.confirmOrder(OrderDetailActivity.this, mClient, new LabAsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(LabResponse response, Object data) {
                                hideNoCancelDialog();
                                MessageUtils.showToast(R.string.ct_order_confirm_suc);
                                getOrderInfo();
                                LocalBroadcastManager.getInstance(OrderDetailActivity.this).sendBroadcast(
                                        new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
                            }

                            @Override
                            public void onFailure(LabResponse response, Object data) {
                                hideNoCancelDialog();
                                    MessageUtils.showToast(response.msg);
                            }
                        }, mOrderInfo.getOid());
                    }
                })
                .setNegativeButton(R.string.ct_cancel, null)
                .show();
    }

    private void beginOrder() {
        showNoCancelDialog();
        OrderBusiness.beginOrder(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideNoCancelDialog();
                MessageUtils.showToast(R.string.ct_order_begin_suc);
                getOrderInfo();
                LocalBroadcastManager.getInstance(OrderDetailActivity.this).sendBroadcast(
                        new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideNoCancelDialog();
                    MessageUtils.showToast(response.msg);
            }
        }, mOrderInfo.getOid());
    }

    private void endOrder() {
        showNoCancelDialog();
        OrderBusiness.endOrder(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                hideNoCancelDialog();
                MessageUtils.showToast(R.string.ct_order_end_suc);
                getOrderInfo();
                LocalBroadcastManager.getInstance(OrderDetailActivity.this).sendBroadcast(
                        new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideNoCancelDialog();
                    MessageUtils.showToast(response.msg);
            }
        }, mOrderInfo.getOid());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PayActivity.isPaySUc(requestCode,resultCode,data)) {
            finish();
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CANCEL:
                case REQUEST_COMMENT:
                case REQUEST_MODIFY:
                    getOrderInfo();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
