package com.cuitrip.app;

import android.os.Bundle;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

/**
 * Created on 7/16.
 */
public class SelfIdentificationActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_id);
        setContentView(R.layout.ct_my_id);
    }
}