package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
public class MessageDetailActivity extends BaseActivity implements View.OnClickListener {

    //TODO 根据订单状态决定是否聊天
    private String mOrderId;
    private ListView mMessageList;
    private AsyncHttpClient mClient = new AsyncHttpClient();
    private DialogList mData;
    private EditText mContent;
    private Button mSend;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mOrderId = intent.getStringExtra(OrderDetailActivity.ORDER_ID);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_message_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_go_order:

                UserInfo info = LoginInstance.getInstance(this).getUserInfo();
                if (info == null || TextUtils.isEmpty(info.getUid())) {
                    MessageUtils.showToast(getString(R.string.ct_please_relogin));
                } else if (mData == null) {
                    MessageUtils.showToast(R.string.loading_text);

                } else if (info.getUid().equals(mData.getInsiderId()) && info.isTravel()) {
                    MessageUtils.showToast(getString(R.string.ct_message_please_switch_mode));
                } else {
                    Intent intent = new Intent(this, OrderDetailActivity.class);
                    intent.putExtra(OrderDetailActivity.ORDER_ID, mOrderId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private LabAsyncHttpResponseHandler mResponseHandler = new LabAsyncHttpResponseHandler(DialogList.class) {
        @Override
        public void onSuccess(LabResponse response, Object data) {
            hideLoading();
            if (data != null) {
                mData = (DialogList) data;
                if (mData.isClosed()) {
                    removeView(R.id.send_block);
                } else {
                    showView(R.id.send_block);
                }
                List<DialogItem> list = mData.getDialog();
                if (list != null && !list.isEmpty()) {
                    MessageDetailListAdapter adapter = new MessageDetailListAdapter(MessageDetailActivity.this,
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
            if (!isTokenInvalid(response)) {
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                }
                return;
            }
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
                                if (!isTokenInvalid(response)) {
                                    MessageUtils.showToast(response.msg);
                                }
                            }
                        }, mOrderId, info.getToken(), info.getUid(), mData.getInsiderId(),
                        content, mData.getSid());
            }
        }
    }
}
