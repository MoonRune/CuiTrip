package com.cuitrip.app;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/6.
 */
public class ConversationListAcitivity extends BaseActivity {
    @InjectView(R.id.ct_recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.ct_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationlist); /*加载您上面的 conversationlist */
        ButterKnife.inject(this);

    }
}
