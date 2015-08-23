package com.cuitrip.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuitrip.app.pro.OrderPersonRenderData;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.imageupload.URLImageParser;
import com.loopj.android.http.AsyncHttpClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baziii on 15/8/17.
 * //todo how to get
 */
public class ServiceFinderInfoAllActivity extends BaseActivity {
    public static final String FINDER_ID = "FINDER_ID";
    @InjectView(R.id.ct_user_ava_im)
    ImageView ctUserAvaIm;
    @InjectView(R.id.ct_user_real_name_tv)
    TextView ctUserRealNameTv;
    @InjectView(R.id.ct_user_birth_tv)
    TextView ctUserBirthTv;
    @InjectView(R.id.ct_user_gender_tv)
    TextView ctUserGenderTv;
    @InjectView(R.id.ct_user_city_tv)
    TextView ctUserCityTv;
    @InjectView(R.id.ct_user_carrer_tv)
    TextView ctUserCarrerTv;
    @InjectView(R.id.ct_user_hobby_tv)
    TextView ctUserHobbyTv;
    @InjectView(R.id.ct_user_language_tv)
    TextView ctUserLanguageTv;
    @InjectView(R.id.ct_user_sign_tv)
    TextView ctUserSignTv;
    @InjectView(R.id.about_hint)
    TextView aboutHint;
    @InjectView(R.id.about_value)
    TextView aboutValue;

    @InjectView(R.id.service_share)
    ImageView serviceShare;
    @InjectView(R.id.ct_user_nick)
    TextView ctUserNick;
    @InjectView(R.id.ct_user_register)
    TextView ctUserRegister;

    String finderId;
    OrderPersonRenderData data;
    boolean isFetching = false;
    AsyncHttpClient mClient = new AsyncHttpClient();
    @InjectView(R.id.ct_user_phone_validate_im)
    ImageView ctUserPhoneValidateIm;
    @InjectView(R.id.ct_user_email_validate_im)
    ImageView ctUserEmailValidateIm;
    @InjectView(R.id.ct_user_identity_validate_im)
    ImageView ctUserIdentityValidateIm;
    @InjectView(R.id.service_back)
    ImageView serviceBack;

    public static void start(Context context, String finderid) {
        context.startActivity(new Intent(context, ServiceFinderInfoAllActivity.class).putExtra(
                FINDER_ID, finderid
        ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_finder_info_all);
        ButterKnife.inject(this);
        finderId = getIntent().getStringExtra(FINDER_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tryRequestUserInfo();
    }

    public void tryRequestUserInfo() {
        if (!isFetching && data == null) {
            requestUserInfo();
        }
    }

    public void requestUserInfo() {
        isFetching = true;
        UserBusiness.getUserInfo(this, mClient, new LabAsyncHttpResponseHandler(UserInfo.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {

                render(OrderPersonRenderData.getInstance(((UserInfo) data)));
                isFetching = false;
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                String msg;
                if (response != null && !TextUtils.isEmpty(response.msg)) {
                    msg = response.msg;
                } else {
                    msg = PlatformUtil.getInstance().getString(R.string.data_error);
                }
                MessageUtils.showToast(msg);
                isFetching = false;

            }
        }, finderId);
    }

    public void setWithDefault(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            text = PlatformUtil.getInstance().getString(R.string.empty_string);
        }
        textView.setText(text);
    }

    @OnClick(R.id.service_back)
    public void onBack() {
        finish();
    }

    @OnClick(R.id.service_share)
    public void onShare() {
    }

    public void setValidated(View view, boolean isValidated) {
        view.setVisibility(isValidated ? View.VISIBLE : View.INVISIBLE);
    }


    public void render(OrderPersonRenderData data) {
        this.data = data;
        ImageHelper.displayPersonImage(data.getUserAva(), ctUserAvaIm, null);
        ctUserNick.setText(data.getUserName());
        ctUserRegister.setText(data.getUserRegisterTime());

        setWithDefault(ctUserRealNameTv, data.getUserRealName());
        setWithDefault(ctUserBirthTv, data.getUserBirth());
        setWithDefault(ctUserGenderTv, data.getUserGender());

        setWithDefault(ctUserCityTv, data.getUserCity());
        setWithDefault(ctUserCarrerTv, data.getUserCarrer());
        setWithDefault(ctUserHobbyTv, data.getUserHobby());
        setWithDefault(ctUserLanguageTv, data.getUserLangeage());
        setWithDefault(ctUserSignTv, data.getUserSign());

        setValidated(ctUserPhoneValidateIm,data.isUserPhoneValidated());
        setValidated(ctUserEmailValidateIm,data.isUserEmailValidated());
        setValidated(ctUserIdentityValidateIm,data.isUserIdentityValidated());

        if (TextUtils.isEmpty(data.getIntroduce())){
            aboutHint.setVisibility(View.GONE);
            aboutValue.setVisibility(View.GONE);
        }else {
            aboutHint.setVisibility(View.VISIBLE);
            aboutValue.setVisibility(View.VISIBLE);
            aboutHint.setText("关于" + data.getUserName());
            String introduce =data.getIntroduce();
            LogHelper.e("omg1",introduce);
            introduce = URLImageParser.replae(introduce);
            LogHelper.e("omg2",introduce);
             introduce = URLImageParser.replaeWidth(introduce);
            LogHelper.e("omg3",introduce);
            URLImageParser urlImageParser = new URLImageParser(aboutValue,this,introduce);
            aboutValue.setText(Html.fromHtml(introduce,urlImageParser,null));
        }
//        ctUserPhoneTv.setText(data.getUserPhone());
//
//        setWithDefault(ctUserEmailTv, data.getUserEmail());
//
//        setWithDefault(ctUserIdentityTv, data.getUserIdentity());

    }

}
