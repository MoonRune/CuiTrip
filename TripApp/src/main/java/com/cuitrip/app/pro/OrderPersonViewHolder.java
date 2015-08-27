package com.cuitrip.app.pro;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuitrip.app.base.PartViewHolder;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.utils.ImageHelper;
import com.lab.utils.Utils;

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
    @InjectView(R.id.ct_user_phone_validate_im)
    ImageView ctUserPhoneValidateIm;
    @InjectView(R.id.ct_user_email_validate_im)
    ImageView ctUserEmailValidateIm;
    @InjectView(R.id.ct_user_identity_validate_im)
    ImageView ctUserIdentityValidateIm;
    @InjectView(R.id.person_top_ll)
    LinearLayout personTopLl;
    @InjectView(R.id.ct_user_phone_validate_ll)
    LinearLayout ctUserPhoneValidateLl;
    @InjectView(R.id.ct_user_phone_validate_div)
    View ctUserPhoneValidateDiv;
    @InjectView(R.id.ct_user_email_validate_ll)
    LinearLayout ctUserEmailValidateLl;
    @InjectView(R.id.ct_user_email_validate_div)
    View ctUserEmailValidateDiv;
    @InjectView(R.id.ct_user_identity_validate_ll)
    LinearLayout ctUserIdentityValidateLl;
    @InjectView(R.id.ct_user_identity_validate_div)
    View ctUserIdentityValidateDiv;
    @InjectView(R.id.ct_user_real_name_ll)
    LinearLayout ctUserRealNameLl;
    @InjectView(R.id.ct_user_real_name_div)
    View ctUserRealNameDiv;
    @InjectView(R.id.ct_user_birth_ll)
    LinearLayout ctUserBirthLl;
    @InjectView(R.id.ct_user_birth_div)
    View ctUserBirthDiv;
    @InjectView(R.id.ct_user_gender_ll)
    LinearLayout ctUserGenderLl;
    @InjectView(R.id.ct_user_gender_div)
    View ctUserGenderDiv;
    @InjectView(R.id.ct_user_city_ll)
    LinearLayout ctUserCityLl;
    @InjectView(R.id.ct_user_city_div)
    View ctUserCityDiv;
    @InjectView(R.id.ct_user_carrer_ll)
    LinearLayout ctUserCarrerLl;
    @InjectView(R.id.ct_user_carrer_div)
    View ctUserCarrerDiv;
    @InjectView(R.id.ct_user_hobby_ll)
    LinearLayout ctUserHobbyLl;
    @InjectView(R.id.ct_user_hobby_div)
    View ctUserHobbyDiv;
    @InjectView(R.id.ct_user_language_ll)
    LinearLayout ctUserLanguageLl;
    @InjectView(R.id.ct_user_language_div)
    View ctUserLanguageDiv;
    @InjectView(R.id.ct_user_sign_ll)
    LinearLayout ctUserSignLl;
    @InjectView(R.id.ct_user_sign_div)
    View ctUserSignDiv;

    public void build(View view) {
        ButterKnife.inject(this, view);
        personTopLl.setVisibility(View.GONE);
    }

    public void setWithDefault(TextView textView, String text, View ll, View div) {
        if (TextUtils.isEmpty(text)) {
            ll.setVisibility(View.GONE);
            div.setVisibility(View.GONE);
            text = PlatformUtil.getInstance().getString(R.string.empty_string);
        } else {

            ll.setVisibility(View.VISIBLE);
            div.setVisibility(View.VISIBLE);
        }
        textView.setText(text);
    }

    public void setValidated(View view, boolean isValidated, View ll, View div) {
        view.setVisibility(isValidated ? View.VISIBLE : View.GONE);
        ll.setVisibility(isValidated ? View.VISIBLE : View.GONE);
        div.setVisibility(isValidated ? View.VISIBLE : View.GONE);
    }


    public void render(OrderPersonRenderData data) {
        ImageHelper.displayPersonImage(data.getUserAva(), ctUserAvaIm, null);
        ctUserNameTv.setText(data.getUserName());
        ctUserRegistTv.setText(data.getUserRegisterTime());

        setWithDefault(ctUserRealNameTv, data.getUserRealName(), ctUserRealNameLl, ctUserRealNameDiv);
        setWithDefault(ctUserBirthTv, data.getUserBirth(), ctUserBirthLl, ctUserBirthDiv);
        setWithDefault(ctUserGenderTv, Utils.getGender(data.getUserGender()), ctUserGenderLl, ctUserGenderDiv);

        setWithDefault(ctUserCityTv, data.getUserCity(), ctUserCityLl, ctUserCityDiv);
        setWithDefault(ctUserCarrerTv, data.getUserCarrer(), ctUserCarrerLl, ctUserCarrerDiv);
        setWithDefault(ctUserHobbyTv, data.getUserHobby(), ctUserHobbyLl, ctUserHobbyDiv);
        setWithDefault(ctUserLanguageTv, data.getUserLangeage(), ctUserLanguageLl, ctUserLanguageDiv);
        setWithDefault(ctUserSignTv, data.getUserSign(), ctUserSignLl, ctUserSignDiv);

        setValidated(ctUserPhoneValidateIm, data.isUserPhoneValidated(), ctUserPhoneValidateLl, ctUserPhoneValidateDiv);
        setValidated(ctUserEmailValidateIm, data.isUserEmailValidated(), ctUserEmailValidateLl, ctUserEmailValidateDiv);
        setValidated(ctUserIdentityValidateIm, data.isUserIdentityValidated(), ctUserIdentityValidateLl, ctUserIdentityValidateDiv);

    }


}
