package com.cuitrip.app;

import android.os.Bundle;
import android.view.View;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.Utils;

/**
 * Created on 7/16.
 */
public class RelationActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_relation_us);
        setContentView(R.layout.ct_relation);
        findViewById(R.id.ct_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.requestDial(RelationActivity.this, "8657186992999");
            }
        });
    }
}
