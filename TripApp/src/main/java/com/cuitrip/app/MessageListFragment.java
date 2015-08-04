package com.cuitrip.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cuitrip.business.MessageBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.MessageItem;
import com.cuitrip.model.UserInfo;
import com.cuitrip.push.MessagePrefs;
import com.cuitrip.service.R;
import com.lab.adapter.BaseListAdapter;
import com.lab.adapter.ViewHolder;
import com.lab.app.BaseFragment;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

/**
 * Created on 7/18.
 */
public class MessageListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        IndexActivity.NewMessageComingListener {

    private static final String TAG = "MessageListFragment";

    public static final String TYPE = "message_type";

    private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    private ListView mMessageList;
    private View mNoLogin;
    private View mEmpty;
    private MessageAdapter mAdapter;
    private List<MessageItem> mMessageDatas;
    private SwipeRefreshLayout mRefresh;

    private int mType = UserInfo.USER_TRAVEL;

    private String mOlderUserId;

    public static MessageListFragment newInstance(int type){
        MessageListFragment fragment = new MessageListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle input = getArguments();
        if(input != null){
            mType = input.getInt(TYPE, 0);
        }
    }

    public void onResume() {
        super.onResume();
        if (LoginInstance.getInstance(getActivity()).isLogin(getActivity())) {
            if(mMessageDatas == null || mMessageDatas.isEmpty()
                    || !mOlderUserId.equals(LoginInstance.getInstance(getActivity()).getUserInfo().getUid())){
                mMessageList.post(new Runnable() {
                    @Override
                    public void run() {
                        getMessageList();
                    }
                });
            }
            mNoLogin.setVisibility(View.GONE);
        }else{
            mNoLogin.setVisibility(View.VISIBLE);
            mNoLogin.findViewById(R.id.ct_login).setOnClickListener(mLoginListener);
        }
        if(getActivity() instanceof IndexActivity) {
            ((IndexActivity) getActivity()).setNewMessageComingListener(this);
        }

    }

    private void getMessageList(){
        UserInfo info = LoginInstance.getInstance(getActivity()).getUserInfo();
        if(getActivity() != null &&  info != null){
            mOlderUserId = info.getUid();

        }
        mRefresh.setRefreshing(true);
        MessageBusiness.getMessageList(getActivity(), mAsyncHttpClient, responseHandler, mType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ct_messag_list, null);
        mMessageList = (ListView)view.findViewById(R.id.ct_list);
        mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        mRefresh.setOnRefreshListener(this);
        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(TextUtils.isEmpty(mAdapter.getItem(i).getOrderId())){
                    return;
                }
                if(view.getTag() != null && view.getTag() instanceof MessageHolder){
                    ((MessageHolder)view.getTag()).mNewMessage.setVisibility(View.GONE);
                }
                startActivity(new Intent(getActivity(), MessageDetailActivity.class)
                        .putExtra(OrderDetailActivity.ORDER_ID, mAdapter.getItem(i).getOrderId()));
            }
        });
        mNoLogin = view.findViewById(R.id.ct_no_login);
        mEmpty = view.findViewById(R.id.ct_empty);
        mAdapter = new MessageAdapter(getActivity(), R.layout.ct_message_item);
        return view;
    }

    protected void onLoginSuccess(){
        mNoLogin.setVisibility(View.GONE);
    }

    private View.OnClickListener mLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            reLogin();
        }
    };

    LabAsyncHttpResponseHandler responseHandler = new LabAsyncHttpResponseHandler() {
        @Override
        public void onFailure(LabResponse response, Object data) {
            mRefresh.setRefreshing(false);
            if(mType == UserInfo.USER_TRAVEL ){
                return;
            }
            if (response != null && !TextUtils.isEmpty(response.msg)) {
                MessageUtils.showToast(response.msg);
            }
        }

        @Override
        public void onSuccess(LabResponse response, Object data) {
            mRefresh.setRefreshing(false);
            if (data != null) {
                try{
                    mMessageDatas = JSON.parseArray(data.toString(), MessageItem.class);
                    mAdapter.setData(mMessageDatas);
                    mMessageList.setAdapter(mAdapter);
                    mMessageList.setEmptyView(mEmpty);
                }catch(Exception e){
                    mEmpty.setVisibility(View.VISIBLE);
                }
            }else{
                mEmpty.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onRefresh() {
        getMessageList();
    }

    @Override
    public void onNewMessage() {
        getMessageList();
    }

    class MessageAdapter extends BaseListAdapter<MessageItem>{

        public MessageAdapter(Context context, int resourceId) {
            super(context, resourceId);
        }

        @Override
        protected ViewHolder view2Holder(View view) {
            MessageHolder holder = new MessageHolder();
            holder.mContent = (TextView)view.findViewById(R.id.content);
            holder.mName = (TextView)view.findViewById(R.id.name);
            holder.mService = (TextView)view.findViewById(R.id.service_name);
            holder.mTime = (TextView)view.findViewById(R.id.message_time);
            holder.mImage = (ImageView)view.findViewById(R.id.author_img);
            holder.mNewMessage = view.findViewById(R.id.message_new);
            return holder;
        }

        @Override
        protected void bindView(ViewHolder holder, MessageItem item, int position) {
            MessageHolder messageHolder = (MessageHolder)holder;
            messageHolder.mContent.setText(item.getLastMsg());
            messageHolder.mName.setText(item.getNick());
            if(TextUtils.isEmpty(item.getGmtCreated())){
                messageHolder.mTime.setText("");
            }else{
                messageHolder.mTime.setText(Utils.getDateFormat(item.getGmtCreated(), Utils.DATE_FORMAT_SECOND));
            }
            messageHolder.mService.setText(item.getTopic());
            if(MessagePrefs.isDialogHasNewMessage(item.getOrderId(),
                    Utils.parseStringToLongTime(item.getGmtCreated(),
                            Utils.DATE_FORMAT_SECOND))){
                messageHolder.mNewMessage.setVisibility(View.VISIBLE);
                if(mType == UserInfo.USER_FINDER){
                    if(mCallback != null){
                        mCallback.onNewMessage(true);
                    }
                }
            }else{
                messageHolder.mNewMessage.setVisibility(View.GONE);
            }
            ImageHelper.displayPersonImage(item.getHeadPic(), messageHolder.mImage, null);
            //TODO
        }
    }

    class MessageHolder extends ViewHolder{
        public ImageView mImage;
        public TextView mName;
        public TextView mContent;
        public TextView mService;
        public TextView mTime;
        public View mNewMessage;
    }

    public interface OnNewMessageCallback{
        void onNewMessage(boolean hasNew);
    }

    private OnNewMessageCallback mCallback;

    /**
     * 对于发现者tab有未读的消息时，在发现者tab展示红点
     * @param mCallback
     */
    public void setOnNewMessageCallback(OnNewMessageCallback mCallback) {
        this.mCallback = mCallback;
    }
}
