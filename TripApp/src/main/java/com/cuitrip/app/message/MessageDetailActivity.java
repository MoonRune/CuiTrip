package com.cuitrip.app.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/19.
 */
public class MessageDetailActivity extends BaseActivity {
    public static final String KEY = "KEY";
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.time)
    TextView time;
    @InjectView(R.id.content)
    TextView content;

    public static void start(Context context, MessageMode messageMode) {
        context.startActivity(new Intent(context, MessageDetailActivity.class).putExtra(KEY, messageMode));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_v);
        ButterKnife.inject(this);
        MessageMode mode = (MessageMode) getIntent().getSerializableExtra(KEY);
        showActionBar(mode.getName());
        title.setText(mode.getName());
        time.setText(Utils.getMsToD(mode.getDate()));
        content.setText(Utils.getMsToD(mode.getContent()));
    }
}
