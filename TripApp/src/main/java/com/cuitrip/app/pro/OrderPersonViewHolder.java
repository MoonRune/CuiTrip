package com.cuitrip.app.pro;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuitrip.app.base.PartViewHolder;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.utils.ImageHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by baziii on 15/8/10.
 */
public class OrderPersonViewHolder implements PartViewHolder<OrderPersonRenderData> {
    public static final int RES = R.layout.ct_order_person;
    @InjectView(R.id.ct_user_ava_im)
    CircleImageView ctUserAvaIm;
    @InjectView(R.id.ct_user_name_tv)
    TextView ctUserNameTv;
    @InjectView(R.id.ct_user_regist_tv)
    TextView ctUserRegistTv;
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
    @InjectView(R.id.ct_user_phone_tv)
    TextView ctUserPhoneTv;
    @InjectView(R.id.ct_user_phone_img)
    ImageView ctUserPhoneImg;
    @InjectView(R.id.ct_user_email_tv)
    TextView ctUserEmailTv;
    @InjectView(R.id.ct_user_email_img)
    ImageView ctUserEmailImg;
    @InjectView(R.id.ct_user_identity_tv)
    TextView ctUserIdentityTv;
    @InjectView(R.id.ct_user_identity_img)
    ImageView ctUserIdentityImg;

    public void build(View view) {
        ButterKnife.inject(this, view);
    }

    public void setWithDefault(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            text = PlatformUtil.getInstance().getString(R.string.empty_string);
        }
        textView.setText(text);
    }

    public void setValidated(ImageView image, boolean isValidate) {
        image.setImageResource(isValidate ? R.drawable.ct_radio_checked_yes
                : R.drawable.ct_radio_checked_no);
    }

    public void render(OrderPersonRenderData data) {
        ImageHelper.displayPersonImage(data.getUserAva(), ctUserAvaIm, null);
        ctUserNameTv.setText(data.getUserName());
        ctUserRegistTv.setText(data.getUserRegisterTime());

        setWithDefault(ctUserRealNameTv, data.getUserRealName());
        setWithDefault(ctUserBirthTv, data.getUserBirth());
        setWithDefault(ctUserGenderTv, data.getUserGender());

        setWithDefault(ctUserCityTv, data.getUserCity());
        setWithDefault(ctUserCarrerTv, data.getUserCarrer());
        setWithDefault(ctUserHobbyTv, data.getUserHobby());
        setWithDefault(ctUserLanguageTv, data.getUserLangeage());
        setWithDefault(ctUserSignTv, data.getUserSign());

        ctUserPhoneTv.setText(data.getUserPhone());
        setValidated(ctUserPhoneImg,data.isUserPhoneValidated());

        setWithDefault(ctUserEmailTv, data.getUserEmail());
        setValidated(ctUserEmailImg,data.isUserEmailValidated());

        setWithDefault(ctUserIdentityTv, data.getUserIdentity());
        setValidated(ctUserIdentityImg,data.isUserIdentityValidated());

    }


}