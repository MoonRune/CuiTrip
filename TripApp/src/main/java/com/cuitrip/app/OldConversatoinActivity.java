package com.cuitrip.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

/**
 * Created on 7/18.
 */
public class OldConversatoinActivity extends BaseActivity implements View.OnClickListener {

    public static final String ORDER_ID = "MessageDetailActivity.ORDER_ID";
    public static final String RECEIVE_ID = "MessageDetailActivity.RECEIVE_ID";
    //TODO 根据订单状态决定是否聊天
    private String mOrderId;
    private ListView mMessageList;
    private AsyncHttpClient mClient = new AsyncHttpClient();
    private DialogList mData;
    private EditText mContent;
    private Button mSend;
    private String mReceiveId;

    public static void start(Context context, String mOrderId,String receiveId) {
        context.startActivity(new Intent(context, OldConversatoinActivity.class)
                .putExtra(ORDER_ID, mOrderId)
        .putExtra(RECEIVE_ID,receiveId));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mOrderId = intent.getStringExtra(ORDER_ID);
        mReceiveId = intent.getStringExtra(RECEIVE_ID);
        if (mOrderId == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(R.string.ct_message);
        setContentView(R.layout.ct_message_detail_list);
        mMessageList = (ListView) findViewById(R.id.ct_list);
        mContent = (EditText) findViewById(R.id.send_content);
        mSend = (Button) findViewById(R.id.message_send);
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
        MessageBusiness.getDialogList(this, mClient, mResponseHandler, mOrderId,
                MessageDetailListAdapter.PAGE_SIZE, 0);
    }

    protected void onLoginSuccess() {
        getMessageList();
    }

    protected void onLoginFailed() {
        finish();
    }


    private LabAsyncHttpResponseHandler mResponseHandler = new LabAsyncHttpResponseHandler(DialogList.class) {
        @Override
        public void onSuccess(LabResponse response, Object data) {
            hideLoading();
            if (data != null) {
                mData = (DialogList) data;
//                if (mData.isClosed()) {
//                    removeView(R.id.send_block);
//                } else {
//                    showView(R.id.send_block);
//                }
                List<DialogItem> list = mData.getDialog();
                if (list != null && !list.isEmpty()) {
                    MessageDetailListAdapter adapter = new MessageDetailListAdapter(OldConversatoinActivity.this,
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
                UserInfo info = LoginInstance.getInstance(this).getUserInfo();
                if (info == null) {
                    return;
                }
                showLoading();
                MessageBusiness.putDialog(this, mClient, new LabAsyncHttpResponseHandler() {
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
