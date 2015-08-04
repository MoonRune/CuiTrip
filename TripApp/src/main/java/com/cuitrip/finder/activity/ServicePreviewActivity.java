package com.cuitrip.finder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cuitrip.app.ServiceDetailActivity;
import com.cuitrip.model.ServiceDetail;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created on 7/24.
 */
public class ServicePreviewActivity extends BaseActivity {

    private AsyncHttpClient mClient = new AsyncHttpClient();

    private ServiceDetail mServiceDetail;
    private String mServiceId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_service_date_edit);
        setContentView(R.layout.ct_activity_service_detail);

        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        String id = intent.getStringExtra(ServiceDetailActivity.SERVICE_ID);
        if (TextUtils.isEmpty(id)) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mServiceId = id;
    }

    protected void onDestroy() {
        super.onDestroy();
        mClient.cancelAllRequests(true);
    }
}
