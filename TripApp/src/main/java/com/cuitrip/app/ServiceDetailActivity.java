package com.cuitrip.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.app.service.ServiceFinderInfoAllActivity;
import com.cuitrip.app.service.ServicePriceDescActivity;
import com.cuitrip.app.service.ServideDetailDescActivity;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.ReviewInfo;
import com.cuitrip.model.ServiceDetail;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.adapter.BasePageAdapter;
import com.lab.app.BaseActivity;
import com.lab.app.DateActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.NumberUtils;
import com.lab.utils.Utils;
import com.lab.utils.share.ShareUtil;
import com.loopj.android.http.AsyncHttpClient;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceDetailActivity extends BaseActivity implements View.OnClickListener {
    public static final String SERVICE_ID = "service_id";
    public static final String USER_TYPE = "user_type";

    private AsyncHttpClient mClient = new AsyncHttpClient();

    private ServiceDetail mServiceDetail;
    private String mServiceId;
    private boolean mIsFinder = false;

    public static void start(Context context, String serviceId) {
        context.startActivity(new Intent(context, ServiceDetailActivity.class).putExtra(SERVICE_ID, serviceId));
    }


    public static void startFinder(Context context, String serviceId) {
        context.startActivity(new Intent(context, ServiceDetailActivity.class).putExtra(SERVICE_ID, serviceId).putExtra(USER_TYPE,
                UserInfo.USER_FINDER));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_activity_service_detail);
        findViewById(R.id.service_back).setOnClickListener(this);

        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        String id = intent.getStringExtra(SERVICE_ID);
        if (TextUtils.isEmpty(id)) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mIsFinder = intent.getIntExtra(USER_TYPE, UserInfo.USER_TRAVEL) == UserInfo.USER_FINDER;
        if (mIsFinder) {
            setViewText(R.id.ct_book, getString(R.string.ct_selected_service_date));
        }
        mServiceId = id;
        initData(id);
    }

    protected void onDestroy() {
        super.onDestroy();
        mClient.cancelAllRequests(true);
    }

    private void initData(String id) {
        showLoading();
        ServiceBusiness.getServiceDetail(this, mClient, new LabAsyncHttpResponseHandler(ServiceDetail.class) {

            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null) {
                    mServiceDetail = (ServiceDetail) data;
                    bind(mServiceDetail);
                } else {
                    MessageUtils.showToast(R.string.network_data_error);
                }
                hideLoading();
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                hideLoading();
                if (!TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                }

            }
        }, id, UnitUtils.getSettingMoneyType());
    }

    private void bind(ServiceDetail data) {
        findViewById(R.id.service_share).setOnClickListener(this);
        findViewById(R.id.service_order_view).setOnClickListener(this);
        findViewById(R.id.ct_book).setOnClickListener(this);

        final TextView index = (TextView) findViewById(R.id.service_index);
        ServiceInfo info = data.getServiceInfo();
        if (info != null) {
            ViewPager pager = (ViewPager) findViewById(R.id.service_pic);
            mAdaper.setData(info.getPic());
            index.setText("1/" + mAdaper.getCount());
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    index.setText((position + 1) + "/" + mAdaper.getCount());
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            pager.setAdapter(mAdaper);

//            setViewText(R.id.service_price, info.getMoneyType() + " " + info.getPrice());
            String price;
            String money = "";

            try {
                double value = Double.valueOf(info.getShowPrice());
                if (value <= 0) {
                    price = getString(R.string.ct_service_free);

                } else {
                    money = info.getShowPrice();
                    if (info.getPriceType() == 1) { //按人计费
                        price = info.getShowCurrency().toUpperCase() + " " + money + getString(R.string.ct_service_unit);
                    } else {
                        price = info.getShowCurrency().toUpperCase() + " " + money;
                    }
                }
            } catch (Exception e) {
                money = info.getShowPrice();
                if (info.getPriceType() == 1) { //按人计费
                    price = info.getShowCurrency().toUpperCase() + " " + money + getString(R.string.ct_service_unit);
                } else {
                    price = info.getShowCurrency().toUpperCase() + " " + money;
                }

            }
            setViewText(R.id.service_price, price);


            setViewText(R.id.service_name, info.getName());
            setViewText(R.id.service_address, info.getAddress());

            RatingBar ratingBar = (RatingBar) findViewById(R.id.service_score);
            ratingBar.setRating(NumberUtils.paserFloat(info.getScore()));

            setViewText(R.id.service_content, info.getDescptWithnoPic());
            setViewText(R.id.service_max_person_value, getString(R.string.per_man_with_num_above, info.getMaxbuyerNum()));

            setViewText(R.id.service_duration_value, getString(R.string.per_hour_with_num_above, info.getServiceTime()));
            //setViewText(R.id.service_best_time_value, info.getBestTime());

            findViewById(R.id.service_click).setOnClickListener(this);
            findViewById(R.id.service_bill_introduce_view).setOnClickListener(this);
            findViewById(R.id.author_more).setOnClickListener(this);

        }
        UserInfo userInfo = data.getUserInfo();
        if (userInfo != null) {
            CircleImageView img = (CircleImageView) findViewById(R.id.author_img);
            ImageHelper.displayPersonImage(userInfo.getHeadPic(), img, null);
            setViewText(R.id.author_name, userInfo.getNick());
            setViewText(R.id.author_sign, userInfo.getSign());
            setViewText(R.id.author_register_time_value, userInfo.getGmtModified());
            //TODO:
            findViewById(R.id.phone_validate).setVisibility(userInfo.isPhoneValidated() ? View.VISIBLE : View.GONE);
            findViewById(R.id.email_validate).setVisibility(userInfo.isEmailValidated() ? View.VISIBLE : View.GONE);
            findViewById(R.id.idcard_validate).setVisibility(userInfo.isIdentityValidated() ? View.VISIBLE : View.GONE);

            if (TextUtils.isEmpty(userInfo.getCareer())) {
                findViewById(R.id.author_career_ll).setVisibility(View.GONE);
                findViewById(R.id.author_career_div).setVisibility(View.GONE);
            } else {
                findViewById(R.id.author_career_ll).setVisibility(View.VISIBLE);
                findViewById(R.id.author_career_div).setVisibility(View.VISIBLE);
                setViewText(R.id.author_career_value, userInfo.getCareer());

            }

            if (TextUtils.isEmpty(userInfo.getInterests())) {
                findViewById(R.id.author_interest_ll).setVisibility(View.GONE);
                findViewById(R.id.author_interest_div).setVisibility(View.GONE);
            } else {
                findViewById(R.id.author_interest_ll).setVisibility(View.VISIBLE);
                findViewById(R.id.author_interest_div).setVisibility(View.VISIBLE);
                setViewText(R.id.author_interest_value, userInfo.getInterests());

            }

            if (TextUtils.isEmpty(userInfo.getLanguage())) {
                findViewById(R.id.author_language_ll).setVisibility(View.GONE);
                findViewById(R.id.author_language_div).setVisibility(View.GONE);
            } else {
                findViewById(R.id.author_language_ll).setVisibility(View.VISIBLE);
                findViewById(R.id.author_language_div).setVisibility(View.VISIBLE);
                setViewText(R.id.author_language_value, userInfo.getLanguage());

            }
            findViewById(R.id.author_img).setOnClickListener(this);
        }

        ReviewInfo review = data.getReviewInfo();
        if (review != null) {
            Integer commentCount = 0;
            try {
                commentCount = Integer.valueOf(review.getReviewNum());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (commentCount > 0) {
                findViewById(R.id.comment_block).setVisibility(View.VISIBLE);
                if (review.getLastReview() != null) {
                    setViewText(R.id.comment_count, review.getLastReview().getNick());
                    setViewText(R.id.comment_time, Utils.getMsToD(review.getLastReview().getGmtCreated()));
                } else {
                    setViewText(R.id.comment_count, "");
                    setViewText(R.id.comment_time, "");
                }
                if (review.getLastReview() != null) {
                    String content = review.getLastReview().getContent();
                    if (TextUtils.isEmpty(content)) {
                        setViewText(R.id.comment_content, getString(R.string.ct_cuitrip_comment_no));
                    } else {
                        setViewText(R.id.comment_content, content);
                    }
                }
                findViewById(R.id.comment_click).setOnClickListener(this);
                ((TextView) findViewById(R.id.comment_click)).setText(getString(R.string.watch_other_comments, commentCount));
            } else {
                findViewById(R.id.comment_block).setVisibility(View.GONE);

            }

        } else {
            findViewById(R.id.comment_block).setVisibility(View.GONE);
            setViewText(R.id.comment_count, "");
            setViewText(R.id.comment_time, "");
            setViewText(R.id.comment_content, getString(R.string.ct_cuitrip_comment_no));
            //TODO:
            findViewById(R.id.comment_click).setOnClickListener(this);
        }
    }

    BasePageAdapter mAdaper = new BasePageAdapter<String>() {

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(ServiceDetailActivity.this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageHelper.displayCtImage(mData != null ? mData.get(position) : null, view, null);
            container.addView(view);
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.service_back:
                onBackPressed();
                break;
            case R.id.service_share:
                share();
                break;
            case R.id.service_click:
                ServideDetailDescActivity.start(this,
                        mServiceDetail.getServiceInfo());
                break;
            case R.id.service_order_view:
                DateActivity.startTraveller(this, mServiceDetail.getServiceInfo().getSid());
                break;
            case R.id.author_more:
                if (LoginInstance.isLogin(this)) {
                    ServiceFinderInfoAllActivity.start(this, mServiceDetail.getServiceInfo().getInsiderId());
                } else {
                    reLogin();
                }
                break;
            case R.id.service_bill_introduce_view:
                ServicePriceDescActivity.start(this,
                        mServiceDetail.getServiceInfo().getPriceType(),
                        mServiceDetail.getServiceInfo().getFeeInclude(),
                        mServiceDetail.getServiceInfo().getFeeExclude());
                break;
            case R.id.comment_click:
                startActivity(new Intent(this, ViewReviewsActivity.class).putExtra(SERVICE_ID, mServiceId));
                break;
            case R.id.ct_book:
                if (LoginInstance.isLogin(this)) {
                    if (mIsFinder) {
                        gotoDate();
                    } else {
                        gotoOrder();
                    }
                } else {
                    reLogin();
                }
                break;
            case R.id.author_img:
                if (LoginInstance.isLogin(this)) {
                    ServiceFinderInfoAllActivity.start(this, mServiceDetail.getServiceInfo().getInsiderId());
                } else {
                    reLogin();
                }
                break;
        }
    }

    protected void onLoginSuccess() {
        gotoOrder();
    }

    private void gotoOrder() {
        startActivity(new Intent(this, CreateOrderActivity.class)
                .putExtra(CreateOrderActivity.SERVICE_INFO, mServiceDetail.getServiceInfo()));
    }

    private void gotoDate() {
        DateActivity.startFinder(this, mServiceDetail.getServiceInfo().getSid());
    }

    private void share() {
        ShareUtil.share(this, findViewById(R.id.service_pic),
                mServiceDetail.getServiceInfo().getName(), String.format(Locale.ENGLISH,
                        getString(R.string.ct_trip_share), mServiceDetail.getServiceInfo().getName()),
                "http://www.cuitrip.com/mobile/serviceDetail.html?sid=" + mServiceId, ""
        );
    }
}
