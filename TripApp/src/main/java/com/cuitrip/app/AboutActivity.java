package com.cuitrip.app;

import android.os.Bundle;
import android.widget.TextView;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.Utils;

/**
 * Created on 7/16.
 */
public class AboutActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_about);
        setContentView(R.layout.ct_about);
        ((TextView)findViewById(R.id.ct_version)).setText(Utils.getAppVersionName(getApplication()));
    }
}
