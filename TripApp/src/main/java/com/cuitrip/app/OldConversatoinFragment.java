package com.cuitrip.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cuitrip.adapter.MessageDetailListAdapter;
import com.cuitrip.business.MessageBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.DialogItem;
import com.cuitrip.model.DialogList;
import com.cuitrip.model.UserInfo;
import com.cuitrip.push.MessagePrefs;
import com.cuitrip.service.R;
import com.lab.app.BaseFragment;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

/**
 * Created on 7/18.
 *
 * em...
 * only used while  1.2.1 message-disable server not published cause ios not passed...
 *  copy from OldConversationActivity
 *  @Deprecated after 1.2.1
 */
@Deprecated
public class OldConversatoinFragment extends BaseFragment implements View.OnClickListener {

    public static final String ORDER_ID = "MessageDetailActivity.ORDER_ID";
    public static final String RECEIVE_ID = "MessageDetailActivity.RECEIVE_ID";
    //TODO 根据订单状态决定是否聊天
    private String mOrderId;
    private ListView mMessageList;
    private AsyncHttpClient mClient = new AsyncHttpClient();
    private DialogList mData;
    private EditText mContent;
    private Button mSend;
    private View sendBlock;
    private String mReceiveId;

    public static OldConversatoinFragment newInstance(String mOrderId,String receiveId) {

        Bundle args = new Bundle();
        args.putString(ORDER_ID, mOrderId);
        args.putString(RECEIVE_ID, receiveId);
        OldConversatoinFragment fragment = new OldConversatoinFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showActionBar(getString(R.string.ct_message));
        View view = inflater.inflate(R.layout.ct_message_detail_list, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOrderId = getArguments().getString(ORDER_ID);
        mReceiveId =  getArguments().getString(RECEIVE_ID);
        mMessageList = (ListView) view.findViewById(R.id.ct_list);
        mContent = (EditText) view.findViewById(R.id.send_content);
        mSend = (Button) view.findViewById(R.id.message_send);
        sendBlock = view.findViewById(R.id.send_block);
        mSend.setOnClickListener(this);
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && editable.length() > 0) {
                    mSend.setEnabled(true);
                } else {
                    mSend.setEnabled(false);
                }
            }
        });
        getMessageList();
        MessagePrefs.setLastDialogReadTime(mOrderId, System.currentTimeMillis());
    }


    private void getMessageList() {
        showLoading();
        MessageBusiness.getDialogList(getActivity(), mClient, mResponseHandler, mOrderId,
                MessageDetailListAdapter.PAGE_SIZE, 0);
    }

    protected void onLoginSuccess() {
        getMessageList();
    }

    protected void onLoginFailed() {

    }


    private LabAsyncHttpResponseHandler mResponseHandler = new LabAsyncHttpResponseHandler(DialogList.class) {
        @Override
        public void onSuccess(LabResponse response, Object data) {
            hideLoading();
            if (data != null) {
                mData = (DialogList) data;
                if (mData.isClosed()) {
                    sendBlock.setVisibility(View.GONE);
                } else {
                    sendBlock.setVisibility(View.VISIBLE);
                }
                List<DialogItem> list = mData.getDialog();
                if (list != null && !list.isEmpty()) {
                    MessageDetailListAdapter adapter = new MessageDetailListAdapter(getActivity(),
                            list, R.layout.ct_list_pending, list.size() <= MessageDetailListAdapter.PAGE_SIZE,
                            mOrderId, mMessageList);
                    mMessageList.setAdapter(adapter);
                    mMessageList.setSelection(mMessageList.getCount() - 1);
                }
            }
        }

        @Override
        public void onFailure(LabResponse response, Object data) {
            hideLoading();
            if (response != null && !TextUtils.isEmpty(response.msg)) {
                MessageUtils.showToast(response.msg);
            }
            return;
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.message_send) {
            if (!TextUtils.isEmpty(mContent.getText())) {
                final String content = mContent.getText().toString();
                UserInfo info = LoginInstance.getInstance(getActivity()).getUserInfo();
                if (info == null) {
                    return;
                }
                showLoading();
                MessageBusiness.putDialog(getActivity(), mClient, new LabAsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(LabResponse response, Object data) {
                                hideLoading();
                                getMessageList();
                                mContent.setText("");
                            }

                            @Override
                            public void onFailure(LabResponse response, Object data) {
                                hideLoading();
                                MessageUtils.showToast(response.msg);
                            }
                        }, mOrderId, info.getToken(), info.getUid(), mReceiveId,
                        content, mData.getSid());
            }
        }
    }
}
