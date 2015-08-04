package com.cuitrip.finder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cuitrip.app.ServiceDetailActivity;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.finder.activity.CreateServiceActivity;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.RecommendOutData;
import com.cuitrip.model.SavedLocalService;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.model.ServiceListInterface;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.adapter.ViewHolder;
import com.lab.app.BaseFragment;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.SavedDescSharedPreferences;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

/**
 * Created on 7/23.
 */
public class ServiceFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ServiceFragment";

    private SwipeRefreshLayout refresh;
    private View mContentView;
    private ListView mListView;
    private View mEmpty;
    private View mNoLogin;
    private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    private List<ServiceInfo> mServices;
    private ServiceAdapter mAdapter;

    private boolean mNeedRefresh = true;

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public void onResume() {
        super.onResume();
        showActionBar(getString(R.string.ct_finder));
        setHasOptionsMenu(true);
        if (LoginInstance.getInstance(getActivity()).isLogin(getActivity())) {
            mNoLogin.setVisibility(View.GONE);
            if (mNeedRefresh) {
                mContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        getServiceList();
                    }
                });
                mNeedRefresh = false;
            }
            if (mAdapter != null) {
                mAdapter.setSaveService(SavedDescSharedPreferences.getSavedServiceDesc(getActivity()));
            }
        } else {
            mNoLogin.setVisibility(View.VISIBLE);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (refresh != null) {
            refresh.setRefreshing(false);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ct_menu_service, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), CreateServiceActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.ct_finder_service_list, container, false);
            mListView = (ListView) mContentView.findViewById(R.id.ct_list);
            mListView.addHeaderView(inflater.inflate(R.layout.ct_list_padding_header, null));
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    position--;
                    ServiceListInterface serviceInfo = mAdapter.getItem(position);
                    if (serviceInfo instanceof SavedLocalService) {
                        ServiceInfo serviceInfo1 = new ServiceInfo();
                        serviceInfo1.setName(serviceInfo.getName());
                        serviceInfo1.setBackPic(serviceInfo.getBackPic());
                        serviceInfo1.setDescpt(serviceInfo.getDescpt());
                        startActivity(new Intent(getActivity(), CreateServiceActivity.class)
                                .putExtra(CreateServiceActivity.SERVICE_INFO, serviceInfo1)
                                .putExtra(CreateServiceActivity.EDIT_MODE, true)
                                .putExtra(CreateServiceActivity.LOCAL_SERVICE, true));
                    } else {
                        ServiceInfo info = (ServiceInfo) serviceInfo;
                        if (info == null) {
                            return;
                        }
                        if (info.getCheckStatus() == 0) {
                            //do nothing
                        } else if (info.getCheckStatus() == 1) {
                            startActivity(new Intent(getActivity(), ServiceDetailActivity.class)
                                    .putExtra(ServiceDetailActivity.SERVICE_ID, info.getSid())
                                    .putExtra(ServiceDetailActivity.USER_TYPE, UserInfo.USER_FINDER));
                        } else if (info.getCheckStatus() == 2) {
                            startActivity(new Intent(getActivity(), CreateServiceActivity.class)
                                    .putExtra(CreateServiceActivity.SERVICE_INFO, info)
                                    .putExtra(CreateServiceActivity.EDIT_MODE, true));
                        }
                    }

                }
            });
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    AlertDialog.Builder builder = MessageUtils.createHoloBuilder(getActivity());
                    builder.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.ct_choice_item, R.id.checktext,
                            new String[]{getResources().getString(R.string.ct_delete), getResources().getString(R.string.ct_cancel)}), new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if (position == 0) {
                                ServiceListInterface info = mAdapter.getItem(i);
                                if (info instanceof SavedLocalService) {
                                    SavedDescSharedPreferences.deleteServiceDesc(getActivity());
                                    mAdapter.setSaveService(null);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    ServiceInfo serviceInfo = (ServiceInfo) info;
                                    deleteService(serviceInfo.getSid());
                                }
                            } else {
                                //do nothing
                            }
                        }
                    });
                    builder.create().show();
                    return true;
                }
            });
            refresh = (SwipeRefreshLayout) mContentView.findViewById(R.id.refresh_layout);
            refresh.setOnRefreshListener(ServiceFragment.this);
            mNoLogin = mContentView.findViewById(R.id.ct_no_login);
            mEmpty = mContentView.findViewById(R.id.ct_empty);
            mEmpty.findViewById(R.id.ct_go_create).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), CreateServiceActivity.class));
                }
            });
            mContentView.findViewById(R.id.ct_login).setOnClickListener(mLoginListener);
            mAdapter = new ServiceAdapter(getActivity(), R.layout.ct_finder_service_list_item);
        } else {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
        }
        return mContentView;
    }

    private void deleteService(String sid) {
        showNoCancelDialog();
        ServiceBusiness.deleteServiceInfo(getActivity(), mAsyncHttpClient, new LabAsyncHttpResponseHandler() {

            @Override
            public void onSuccess(LabResponse response, Object data) {
                MessageUtils.showToast(getActivity().getString(R.string.ct_delete_suc));
                hideNoCancelDialog();
                getServiceList();
            }

            @Override
            public void onFailure(LabResponse response, Object data) {

                hideNoCancelDialog();
                MessageUtils.showToast(getActivity().getString(R.string.ct_delete_failed_can_try));
            }
        }, sid);
    }


    private View.OnClickListener mLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            reLogin();
        }
    };

    LabAsyncHttpResponseHandler responseHandler = new LabAsyncHttpResponseHandler(RecommendOutData.class) {
        @Override
        public void onFailure(LabResponse response, Object data) {
            if (response != null && !TextUtils.isEmpty(response.msg)) {
                MessageUtils.showToast(response.msg);
            }
            refresh.setRefreshing(false);
            onNetwokError(0, 0, 0);
        }

        @Override
        public void onSuccess(LabResponse response, Object data) {
            refresh.setRefreshing(false);
            if (data != null) {
                try {
                    mServices = JSON.parseArray(data.toString(), ServiceInfo.class);
                    mAdapter.setData(mServices);
                    mListView.setEmptyView(mEmpty);
                    mListView.setAdapter(mAdapter);
                } catch (Exception e) {
                    mEmpty.setVisibility(View.VISIBLE);
                }
            } else {
                mEmpty.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onLoginSuccess() {
        super.onLoginSuccess();
        mNeedRefresh = true;
    }

    @Override
    public void onRefresh() {
        getServiceList();
    }

    private void getServiceList() {
        refresh.setRefreshing(true);
        ServiceBusiness.getServiceList(getActivity(), mAsyncHttpClient, responseHandler);
    }

    class ServiceAdapter extends BaseAdapter {
        protected List<ServiceInfo> mItems;
        protected LayoutInflater mInflater;
        protected int mResource;

        private String mUserPic;
        private SavedLocalService mSaveService;

        public ServiceAdapter(Context context, int resourceId) {
            mSaveService = SavedDescSharedPreferences.getSavedServiceDesc(context);
            mResource = resourceId;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mUserPic = LoginInstance.getInstance(context).getUserInfo().getHeadPic();
        }

        public void setSaveService(SavedLocalService mSaveService) {
            this.mSaveService = mSaveService;
            notifyDataSetChanged();
        }

        public void setData(List<ServiceInfo> items) {
            mItems = items;
        }

        public List<ServiceInfo> getData() {
            return mItems;
        }

        @Override
        public int getCount() {
            if (mSaveService == null) {
                return mItems == null ? 0 : mItems.size();
            } else {
                return mItems == null ? 1 : mItems.size() + 1;
            }
        }

        @Override
        public ServiceListInterface getItem(int i) {
            if (mSaveService == null) {
                if (i < 0 || mItems == null || i >= mItems.size()) {
                    return null;
                }
                return mItems.get(i);
            } else {
                if (i == 0) {
                    return mSaveService;
                } else {
                    if (i < 1 || mItems == null || i > mItems.size()) {
                        return null;
                    }
                    return mItems.get(i - 1);
                }
            }

        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (null == view) {
                view = mInflater.inflate(mResource, null);
                view.setTag(view2Holder(view));
            }
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            bindView(viewHolder, getItem(i), i);
            return view;
        }

        protected ViewHolder view2Holder(View view) {
            ServiceViewHolder holder = new ServiceViewHolder();
            holder.mDate = view.findViewById(R.id.ct_date);
            holder.mServiceChecking = view.findViewById(R.id.service_checking);
            holder.mServiceName = (TextView) view.findViewById(R.id.service_name);
            holder.mServiceStatus = (TextView) view.findViewById(R.id.service_status);
            holder.mServiceReason = (TextView) view.findViewById(R.id.service_reason);
            holder.mAuthor = (ImageView) view.findViewById(R.id.author_img);
            holder.mServicePic = (ImageView) view.findViewById(R.id.service_img);
            return holder;
        }

        protected void bindView(ViewHolder holder, ServiceListInterface serviceItem, int position) {
            ServiceViewHolder serviceViewHolder = (ServiceViewHolder) holder;
            serviceViewHolder.mServiceName.setText(serviceItem.getName());
            ImageHelper.displayCtImage(serviceItem.getBackPic(), serviceViewHolder.mServicePic, null);
            ImageHelper.displayPersonImage(mUserPic, serviceViewHolder.mAuthor, null);
            if (serviceItem instanceof SavedLocalService) {
                serviceViewHolder.mDate.setVisibility(View.GONE);
                serviceViewHolder.mServiceReason.setVisibility(View.GONE);
                serviceViewHolder.mServiceChecking.setVisibility(View.GONE);
                serviceViewHolder.mServiceStatus.setVisibility(View.VISIBLE);
                serviceViewHolder.mServiceStatus.setText(R.string.ct_service_to_edit);
            } else {
                ServiceInfo item = (ServiceInfo) serviceItem;
                if (item.getCheckStatus() == 0) {
                    serviceViewHolder.mDate.setVisibility(View.GONE);
                    serviceViewHolder.mServiceStatus.setVisibility(View.GONE);
                    serviceViewHolder.mServiceReason.setVisibility(View.GONE);
                    serviceViewHolder.mServiceChecking.setVisibility(View.VISIBLE);
                } else if (item.getCheckStatus() == 1) {
                    serviceViewHolder.mDate.setVisibility(View.VISIBLE);
                    serviceViewHolder.mServiceStatus.setVisibility(View.GONE);
                    serviceViewHolder.mServiceReason.setVisibility(View.GONE);
                    serviceViewHolder.mServiceChecking.setVisibility(View.GONE);
                } else if (item.getCheckStatus() == 2) {
                    serviceViewHolder.mDate.setVisibility(View.GONE);
                    serviceViewHolder.mServiceStatus.setVisibility(View.VISIBLE);
                    serviceViewHolder.mServiceStatus.setText(R.string.ct_service_no_pass);
                    if (item.getExtInfoObject() != null && !TextUtils.isEmpty(item.getExtInfoObject().getRefuseReason())) {
                        serviceViewHolder.mServiceReason.setVisibility(View.VISIBLE);
                        serviceViewHolder.mServiceReason.setText(item.getExtInfoObject().getRefuseReason());
                    } else {
                        serviceViewHolder.mServiceReason.setVisibility(View.GONE);
                    }
                    serviceViewHolder.mServiceChecking.setVisibility(View.GONE);
                }
            }
        }
    }

    class ServiceViewHolder extends ViewHolder {
        public ImageView mServicePic;
        public ImageView mAuthor;
        public TextView mServiceName;
        public TextView mServiceStatus;
        public TextView mServiceReason;
        public View mDate;
        public View mServiceChecking;
    }
}
