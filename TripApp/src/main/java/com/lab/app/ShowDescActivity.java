package com.lab.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.MessageUtils;

public class ShowDescActivity extends BaseActivity {
    public static final String SERVICE_DESC = "SERVICE_DESC";
    public static final String SERVICE_TITLE = "SERVICE_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        String id = intent.getStringExtra(SERVICE_DESC);
        if (TextUtils.isEmpty(id)) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        if (!TextUtils.isEmpty(intent.getStringExtra(SERVICE_TITLE))) {
            showActionBar(intent.getStringExtra(SERVICE_TITLE));
        }

        setContentView(R.layout.ct_activity_desc);
        setViewText(R.id.desc, id);
    }
}
