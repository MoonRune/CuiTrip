package com.cuitrip.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cuitrip.app.CreateOrderActivity;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.MessageUtils;
import com.lab.utils.NumberUtils;
import com.lab.utils.imageupload.URLImageParser;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/17.
 */
public class ServideDetailDescActivity extends BaseActivity {
    public static final String SERVIDE_ID = "ServideDetailDescActivity";
    @InjectView(R.id.ce_service_name)
    TextView ceServiceName;
    @InjectView(R.id.ce_service_location)
    TextView ceServiceLocation;
    @InjectView(R.id.service_score)
    RatingBar serviceScore;
    @InjectView(R.id.ct_content_v)
    TextView ctContentV;
    @InjectView(R.id.ct_book)
    Button ctBook;
    ServiceInfo serviceInfo;

    public static void start(Context context, ServiceInfo serviceInfo) {

        context.startActivity(new Intent(context, ServideDetailDescActivity.class)
                .putExtra(SERVIDE_ID, serviceInfo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_service_desc_detail);
        ButterKnife.inject(this);
        if (getIntent().hasExtra(SERVIDE_ID) && getIntent().getSerializableExtra(SERVIDE_ID) instanceof ServiceInfo) {
            serviceInfo = (ServiceInfo) getIntent().getSerializableExtra(SERVIDE_ID);
        } else {
            MessageUtils.showToast(R.string.data_error);
            finish();
            return;
        }
        showActionBar(getString(R.string.service_detail_desc_title));
        ceServiceName.setText(serviceInfo.getName());
        ceServiceLocation.setText(serviceInfo.getAddress());
        serviceScore.setRating(NumberUtils.paserFloat(serviceInfo.getScore()));
        String introduce = URLImageParser.replae(serviceInfo.getDescpt());
        introduce = URLImageParser.replaeWidth(serviceInfo.getDescpt());
        URLImageParser p = new URLImageParser(ctContentV, ServideDetailDescActivity.this, introduce);
        ctContentV.setText(Html.fromHtml(introduce, p, null));
        ctBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateOrderActivity.start(ServideDetailDescActivity.this, serviceInfo);
            }
        });
    }
}
