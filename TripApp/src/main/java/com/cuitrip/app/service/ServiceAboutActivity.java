package com.cuitrip.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuitrip.app.ServiceDetailActivity;
import com.cuitrip.finder.activity.CreateServiceActivity;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.app.DateActivity;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/14.
 */
public class ServiceAboutActivity extends BaseActivity implements IServiceAboutView {

    public static final String SERVICE_ID = "DateActivity.SERVICE_ID";

    @InjectView(R.id.ct_service_ava_img)
    ImageView ctServiceAvaImg;
    @InjectView(R.id.ct_service_name)
    TextView ctServiceName;
    @InjectView(R.id.ct_service_address)
    TextView ctServiceAddress;
    @InjectView(R.id.ct_service_create)
    TextView ctServiceCreate;
    @InjectView(R.id.total_visit_amount_tv)
    TextView totalVisitAmountTv;
    @InjectView(R.id.daily_visit_tv)
    TextView dailyVisitTv;
    @InjectView(R.id.total_like_tv)
    TextView totalLikeTv;
    @InjectView(R.id.total_visitor_num_tv)
    TextView totalVisitorNumTv;
    @InjectView(R.id.order_size_tv)
    TextView orderSizeTv;

    ServiceAboutPresent serviceAboutPresent;
    public static void start(Context context, String serviceId) {
        context.startActivity(new Intent(context, ServiceAboutActivity.class).putExtra(SERVICE_ID, serviceId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_modify,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_OK:
                serviceAboutPresent.clickModify();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(getString(R.string.ct_service_about_title_default));
        setContentView(R.layout.ct_service_about);
        ButterKnife.inject(this);
        serviceAboutPresent = new ServiceAboutPresent(this,getIntent().getStringExtra(SERVICE_ID));
        serviceAboutPresent.requestServiceAboutData();
    }

    @Override
    public void jumpService(String serviceId) {
        ServiceDetailActivity.startFinder(this, serviceId);
    }

    @Override
    public void jumpFinderSetDate(String serviceId) {
        DateActivity.startFinder(this, serviceId);
    }

    @Override
    public void showError(String error) {
        MessageUtils.showToast(error);
    }

    @Override
    public void jumpModifyService(ServiceInfo serviceInfo) {
        CreateServiceActivity.startModifyRemote(this,serviceInfo);
    }

    @Override
    public void uiShowRefreshLoading() {
        showNoCancelDialog();
    }

    @Override
    public void uiHideRefreshLoading() {
        hideNoCancelDialog();
    }

    @Override
    public void renderUIWithData(ServiceAboutMode item) {
        showActionBar(String.valueOf(item.getServiceName()));
        ImageHelper.displayCtImage(String.valueOf(item.getServiceAva()),ctServiceAvaImg,null);
        ctServiceName.setText(item.getServiceName());
        ctServiceAddress.setText(item.getServiceAddress());
        ctServiceCreate.setText(item.getServiceCreateDate());

        totalVisitAmountTv.setText(item.getWholeVisitAmount());
        dailyVisitTv.setText(item.getTodayVisitAmout());
        totalLikeTv.setText(item.getLikedPeople());

        totalVisitorNumTv.setText(item.getWholeVisitPeopleSize());
        orderSizeTv.setText(item.getWholeOrderAmount());

    }

    @OnClick(R.id.build_service_time_v)
    public void buildOrderTime(){
       serviceAboutPresent.clickSetDate();
    }

    @OnClick(R.id.service_block)
      public void serviceDetail(){
        serviceAboutPresent.clickService();
    }

    @Override
    public void disableEvent() {

    }

    @Override
    public void enableEvent() {

    }
}
