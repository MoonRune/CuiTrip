package com.cuitrip.finder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cuitrip.app.IndexActivity;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

/**
 * Created on 7/28.
 */
public class CreateServiceSuccessActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_service_create_sucess);
        setContentView(R.layout.ct_create_service_sucess);
        findViewById(R.id.go_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateServiceSuccessActivity.this, IndexActivity.class)
                        .putExtra(IndexActivity.GO_TO_TAB, IndexActivity.SERVICE_TAB));
                finish();
            }
        });
        findViewById(R.id.new_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateServiceSuccessActivity.this, CreateServiceActivity.class));
                finish();
            }
        });
    }
}
