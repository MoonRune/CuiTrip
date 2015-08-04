package com.lab.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;

import com.cuitrip.service.R;

public abstract class BaseTabHostActivity extends BaseActivity {
    protected FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        initTabs();
    }

    public int getLayoutResource(){
        return R.layout.ct_fragment_tabs;
    }

    protected abstract void initTabs();
}
