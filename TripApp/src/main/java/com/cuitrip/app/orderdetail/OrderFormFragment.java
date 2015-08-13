package com.cuitrip.app.orderdetail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.cuitrip.app.CancelOrderActivity;
import com.cuitrip.app.CommentActivity;
import com.cuitrip.app.ModifyOrderActivity;
import com.cuitrip.app.OrderFragment;
import com.cuitrip.app.PayActivity;
import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.PartViewHolder;
import com.cuitrip.app.orderdetail.orderstatus.BaseOrderFormPresent;
import com.cuitrip.app.orderdetail.orderstatus.IOrderFetcher;
import com.cuitrip.app.orderdetail.orderstatus.IOrderFormPresent;
import com.cuitrip.app.orderdetail.orderstatus.traveller.TravellerOverPresent;
import com.cuitrip.app.orderdetail.orderstatus.traveller.TravellerUnvaliablePresent;
import com.cuitrip.app.orderdetail.orderstatus.traveller.TravellerWaitCommentPresent;
import com.cuitrip.app.orderdetail.orderstatus.traveller.TravellerWaitConfirmPresent;
import com.cuitrip.app.orderdetail.orderstatus.traveller.TravellerWaitEndPresent;
import com.cuitrip.app.orderdetail.orderstatus.traveller.TravellerWaitPayPresent;
import com.cuitrip.app.orderdetail.orderstatus.traveller.TravellerWaitStartPresent;
import com.cuitrip.app.orderdetail.orderstatus.finder.FinderOverPresent;
import com.cuitrip.app.orderdetail.orderstatus.finder.FinderUnvaliablePresent;
import com.cuitrip.app.orderdetail.orderstatus.finder.FinderWaitCommentPresent;
import com.cuitrip.app.orderdetail.orderstatus.finder.FinderWaitConfirmPresent;
import com.cuitrip.app.orderdetail.orderstatus.finder.FinderWaitEndPresent;
import com.cuitrip.app.orderdetail.orderstatus.finder.FinderWaitPayPresent;
import com.cuitrip.app.orderdetail.orderstatus.finder.FinderWaitStartPresent;
import com.cuitrip.app.pro.CommentPartRenderData;
import com.cuitrip.app.pro.CommentPartViewHolder;
import com.cuitrip.app.pro.OrderProgressingRenderData;
import com.cuitrip.app.pro.OrderProgressingViewHolder;
import com.cuitrip.app.pro.ServicePartRenderData;
import com.cuitrip.app.pro.ServicePartViewHolder;
import com.cuitrip.business.OrderBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.OrderItem;
import com.cuitrip.service.R;
import com.lab.app.BaseFragment;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/11.
 */
public class OrderFormFragment extends BaseFragment {

    public static final String TAG = "OrderFormFragment";
    public static final String ORDER_KEY = "OrderFormFragment.ORDER_KEY";
    MenuItem mOptionMenuItem;
    String mActionTitle;
    IOrderDetailView mOrderDetailView;
    AsyncHttpClient mClient = new AsyncHttpClient();

    StubViewHolder<ServicePartRenderData> mServiceStub = new StubViewHolder(new ServicePartViewHolder());
    StubViewHolder<CommentPartRenderData>  mCommentstub = new StubViewHolder(new CommentPartViewHolder());
    StubViewHolder<OrderProgressingRenderData>  mProgressingStub = new StubViewHolder(new OrderProgressingViewHolder());


    @InjectView(R.id.ct_service_info_v)
    ViewStub ctServiceInfoV;
    @InjectView(R.id.ct_service_comment_v)
    ViewStub ctServiceCommentV;
    @InjectView(R.id.ct_bottom_tv)
    TextView ctBottomTv;
    BaseOrderFormPresent baseOrderFormPresent;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public class StubViewHolder<T> implements PartViewHolder<T> {
        PartViewHolder<T> partViewHolder;
        ViewStub stub;
        View inflatedView;
        boolean isInflated = false;

        public StubViewHolder(PartViewHolder<T> partViewHolder) {
            this.partViewHolder = partViewHolder;
        }

        public PartViewHolder<T> getPartViewHolder() {
            return partViewHolder;
        }

        @Override
        public void build(View view) {
            stub = (ViewStub) view;

        }

        @Override
        public void render(T o) {
            if (o != null) {
                if (!isInflated) {
                    isInflated = true;
                    inflatedView = stub.inflate();
                    partViewHolder.build(inflatedView);
                    partViewHolder.render(o);
                } else {
                    partViewHolder.render(o);
                }
            }
        }

        void show() {
            if (isInflated) {
                inflatedView.setVisibility(View.VISIBLE);
            } else {
                stub.setVisibility(View.VISIBLE);
            }
        }

        void hide() {
            if (isInflated) {
                inflatedView.setVisibility(View.GONE);
            } else {
                stub.setVisibility(View.GONE);
            }
        }
    }

    public void showOption(String text) {
        mActionTitle = text;
        getActivity().supportInvalidateOptionsMenu();
    }

    public void hideOptionMenu() {
        mActionTitle = null;
        getActivity().supportInvalidateOptionsMenu();

    }

    public static OrderFormFragment newInstance(OrderItem orderItem) {
        Bundle args = new Bundle();
        args.putSerializable(ORDER_KEY, orderItem);
        OrderFormFragment fragment = new OrderFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void refresh(OrderItem orderItem){
        getArguments().putSerializable(ORDER_KEY,orderItem);
        if (ctBottomTv!=null) {
            renderWithData();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ct_order_form, menu);
        mOptionMenuItem = menu.findItem(R.id.action);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (TextUtils.isEmpty(mActionTitle)) {
            mOptionMenuItem.setVisible(false);
        } else {
            mOptionMenuItem.setVisible(true);
            mOptionMenuItem.setTitle(mActionTitle);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                baseOrderFormPresent.clickMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.ct_bottom_tv)
    public void onBottomCLick() {
        baseOrderFormPresent.clickBottom();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ct_order_form_order, null);
        ButterKnife.inject(this, view);
        mServiceStub.build(ctServiceInfoV);
        mCommentstub.build(ctServiceCommentV);
        return view;
    }

    public void renderWithData() {
        if (getArguments().containsKey(ORDER_KEY)) {
            OrderItem orderItem = (OrderItem) getArguments().getSerializable(ORDER_KEY);
            boolean isImTravel = LoginInstance.getInstance(getActivity()).getUserInfo().isTravel();
            mOrderDetailView = isImTravel ? new TravellerOrderDetailView() : new FinderOrderDetailView();
//            mOrderDetailView = new FinderOrderDetailView();
            mOrderDetailView.requestPresentRender(orderItem);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        renderWithData();
    }
    IOrderFetcher orderFetcher = new IOrderFetcher() {
        @Override
        public void cancelOrder(OrderItem orderItem, final String reason, final CtApiCallback callback) {
            showLoading();
            OrderBusiness.cancelOrder(getActivity(), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    callback.onSuc();
                    hideLoading();
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    callback.onFailed(new CtException(response.msg));
                    hideLoading();

                }
            },orderItem.getOid(),reason);

        }

        @Override
        public void confirmOrder(OrderItem orderItem, final CtApiCallback callback) {
            showLoading();
            OrderBusiness.confirmOrder(getActivity(), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    callback.onSuc();
                    hideLoading();
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    callback.onFailed(new CtException(response.msg));
                    hideLoading();

                }
            }, orderItem.getOid());
        }

        @Override
        public void startOrder(OrderItem orderItem, final CtApiCallback callback) {
            showLoading();
            OrderBusiness.beginOrder(getActivity(), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    callback.onSuc();
                    hideLoading();
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    callback.onFailed(new CtException(response.msg));
                    hideLoading();

                }
            },orderItem.getOid());
        }
    };

    public class TravelProxyPresent extends IOrderFormPresent<ITravelerOrderDetailView> {

        public TravelProxyPresent(ITravelerOrderDetailView orderDetailView) {
            super(orderDetailView);
        }

        public void changeOrderItem(OrderItem orderItem) {
            setOrderItem(orderItem);
            switch (orderItem.getStatus()) {
                case OrderItem.STATUS_WAIT_COFIRM:
                    baseOrderFormPresent = new TravellerWaitConfirmPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_WAIT_PAY:
                    baseOrderFormPresent = new TravellerWaitPayPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_WAIT_START:
                    baseOrderFormPresent = new TravellerWaitStartPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_WAIT_END:
                    baseOrderFormPresent = new TravellerWaitEndPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_WAIT_COMMENT:
                    baseOrderFormPresent = new TravellerWaitCommentPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_OVER:
                    baseOrderFormPresent = new TravellerOverPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_UNVALIABLE:
                    baseOrderFormPresent = new TravellerUnvaliablePresent(mOrderDetailView, orderItem);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void render() {
            baseOrderFormPresent.render();
        }

        @Override
        public void clickBottom() {
            baseOrderFormPresent.clickBottom();
        }

        @Override
        public void clickMenu() {
            baseOrderFormPresent.clickMenu();
        }

    }

    public class FinderProxyPresent extends IOrderFormPresent<IFinderOrderDetailView> {

        public FinderProxyPresent(IFinderOrderDetailView orderDetailView) {
            super(orderDetailView);
        }

        public void changeOrderItem(OrderItem orderItem) {
            setOrderItem(orderItem);
            switch (orderItem.getStatus()) {
                case OrderItem.STATUS_WAIT_COFIRM:
                    baseOrderFormPresent = new FinderWaitConfirmPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_WAIT_PAY:
                    baseOrderFormPresent = new FinderWaitPayPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_WAIT_START:
                    baseOrderFormPresent = new FinderWaitStartPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_WAIT_END:
                    baseOrderFormPresent = new FinderWaitEndPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_WAIT_COMMENT:
                    baseOrderFormPresent = new FinderWaitCommentPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_OVER:
                    baseOrderFormPresent = new FinderOverPresent(mOrderDetailView, orderItem);
                    break;
                case OrderItem.STATUS_UNVALIABLE:
                    baseOrderFormPresent = new FinderUnvaliablePresent(mOrderDetailView, orderItem);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void render() {
            baseOrderFormPresent.render();
        }

        @Override
        public void clickBottom() {
            baseOrderFormPresent.clickBottom();
        }

        @Override
        public void clickMenu() {
            baseOrderFormPresent.clickMenu();
        }

    }

    public class FinderOrderDetailView implements IFinderOrderDetailView {
        FinderProxyPresent present = new FinderProxyPresent(this);
        @Override
        public void showLoading() {
            OrderFormFragment.this.showLoading();
        }

        @Override
        public void hideLoading() {
            OrderFormFragment.this.hideLoading();
        }

        @Override
        public void renderUi(OrderMode orderMode) {
            LogHelper.e("omg", "FinderOrderDetailView renderUi");
            mServiceStub.render(orderMode.getServiceData());
            mCommentstub.render(orderMode.getCommentData());
            mProgressingStub.render(orderMode.getProgressData());
            showOption(orderMode.getMenuText());
            if (TextUtils.isEmpty(orderMode.getBottomText())) {
                ctBottomTv.setVisibility(View.GONE);
            } else {
                ctBottomTv.setVisibility(View.VISIBLE);
                ctBottomTv.setText(orderMode.getBottomText());
                ctBottomTv.setEnabled(orderMode.isBottomEnable());
            }
        }

        @Override
        public void requestPresentRender(OrderItem orderItem) {
            present.changeOrderItem(orderItem);
            present.render();

        }

        @Override
        public void jumpConfirmOrder(final OrderItem orderItem) {

            MessageUtils.createHoloBuilder(getActivity()).setMessage(R.string.ct_order_confirm)
                    .setPositiveButton(R.string.ct_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showNoCancelDialog();
                            OrderBusiness.confirmOrder(getActivity(), mClient, new LabAsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(LabResponse response, Object data) {
                                    hideNoCancelDialog();
                                    MessageUtils.showToast(R.string.ct_order_confirm_suc);
                                    ((OrderFormActivity) getActivity()).requestOrderDetail();
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
                                            new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
                                }

                                @Override
                                public void onFailure(LabResponse response, Object data) {
                                    hideNoCancelDialog();
                                    MessageUtils.showToast(response.msg);
                                }
                            }, orderItem.getOid());
                        }
                    })
                    .setNegativeButton(R.string.ct_cancel, null)
                    .show();
        }

        @Override
        public void jumpCancelOrder(OrderItem orderItem) {
            CancelOrderActivity.start(getActivity(),orderItem);

        }

        @Override
        public void jumpRefuseOrder(OrderItem orderItem) {

        }

        @Override
        public void jumpStartOrder(final  OrderItem orderItem) {
            showNoCancelDialog();
            OrderBusiness.beginOrder(getActivity(), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    hideNoCancelDialog();
                    MessageUtils.showToast(R.string.ct_order_begin_suc);
                    ((OrderFormActivity) getActivity()).requestOrderDetail();
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
                            new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    hideNoCancelDialog();
                    MessageUtils.showToast(response.msg);
                }
            }, orderItem.getOid());

        }

        @Override
        public void jumpHelp(OrderItem orderItem) {

        }

        @Override
        public void jumpEndOrder(final OrderItem orderItem) {

            showNoCancelDialog();
            OrderBusiness.endOrder(getActivity(), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    hideNoCancelDialog();
                    MessageUtils.showToast(R.string.ct_order_end_suc);
                    ((OrderFormActivity) getActivity()).requestOrderDetail();
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
                            new Intent(OrderFragment.ORDER_STATUS_CHANGED_ACTION));
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    hideNoCancelDialog();
                    MessageUtils.showToast(response.msg);
                }
            }, orderItem.getOid());
        }
    }

    public class TravellerOrderDetailView implements ITravelerOrderDetailView {
        TravelProxyPresent present = new TravelProxyPresent(this);


        @Override
        public void showLoading() {
            OrderFormFragment.this.showLoading();
        }

        @Override
        public void hideLoading() {
            OrderFormFragment.this.hideLoading();
        }

        @Override
        public void renderUi(OrderMode orderMode) {
            LogHelper.e("omg", "FinderOrderDetailView renderUi");
            mServiceStub.render(orderMode.getServiceData());
            mCommentstub.render(orderMode.getCommentData());
            mProgressingStub.render(orderMode.getProgressData());
            showOption(orderMode.getMenuText());
            if (TextUtils.isEmpty(orderMode.getBottomText())) {
                ctBottomTv.setVisibility(View.GONE);
            } else {
                ctBottomTv.setVisibility(View.VISIBLE);
                ctBottomTv.setText(orderMode.getBottomText());
                ctBottomTv.setEnabled(orderMode.isBottomEnable());
            }
        }

        @Override
        public void requestPresentRender(OrderItem orderItem) {
            present.changeOrderItem(orderItem);
            present.render();

        }

        @Override
        public void jumpModifyOrder(OrderItem orderItem) {
            LogHelper.e("omg", "jumpModifyOrder");
            ModifyOrderActivity.startModify(getActivity(),orderItem);
        }

        @Override
        public void jumpCancelOrder(OrderItem orderItem) {
            LogHelper.e("omg", "jumpCancelOrder");
            CancelOrderActivity.start(getActivity(),orderItem);
        }

        @Override
        public void jumpPayOrder(OrderItem orderItem) {
            PayActivity.startActivity(getActivity(), orderItem.getOid());
            LogHelper.e("omg", "jumpPayOrder");

        }

        @Override
        public void jumpMapOrder(OrderItem orderItem) {
            LogHelper.e("omg", "jumpMapOrder");

        }

        @Override
        public void jumpCommentOrder(OrderItem orderItem) {
            CommentActivity.start(getActivity(), orderItem);
            LogHelper.e("omg", "jumpCommentOrder");

        }

        @Override
        public void jumpHelp(OrderItem orderItem) {

        }


    }
}
