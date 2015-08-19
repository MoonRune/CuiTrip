package com.cuitrip.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.cuitrip.app.identity.SelfIdentificationActivity;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.GetImageHelper;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.Utils;
import com.lab.utils.imageupload.IImageUploader;
import com.lab.utils.imageupload.ImageUploadCallback;
import com.lab.utils.imageupload.imp.ServiceImageUploader;
import com.loopj.android.http.AsyncHttpClient;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created on 7/16.
 */
public class SelfActivity extends BaseActivity implements View.OnClickListener, OnFocusChangeListener {
    private static final int REQUEST_IMAGE = 100;
    public static final String TAG = "SelfActivity";

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'ct_my_profile.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */

    @InjectView(R.id.ava_user_nick_tv)
    TextView mPersonalAvaNickTv;
    @InjectView(R.id.ava_user_regist_tv)
    TextView mPersonalAvaRegistTv;
    @InjectView(R.id.ct_personal_ava_iv)
    CircleImageView mPersonalAvaIv;

    @InjectView(R.id.ct_personal_birth_et)
    EditText mPersonalBirthEt;
    @InjectView(R.id.ct_personal_name_et)
    EditText mPersonalNameEt;
    @InjectView(R.id.ct_personal_nick_et)
    EditText mPersonalNickEt;
    @InjectView(R.id.ct_personal_gender_tv)
    TextView mPersonalGenderTv;
    @InjectView(R.id.ct_gender_arrow_v)
    View mPersonalGenderArrowV;

    @InjectView(R.id.ct_personal_area_et)
    TextView mPersonalAreaEt;
    @InjectView(R.id.ct_personal_job_et)
    EditText mPersonalJobEt;
    @InjectView(R.id.ct_personal_hobby_et)
    EditText mPersonalHobbyEt;
    @InjectView(R.id.ct_personal_language_et)
    EditText mPersonalLanguageEt;
    @InjectView(R.id.ct_personal_sign_et)
    EditText mPersonalSignEt;
    @InjectView(R.id.ct_personal_gender_ll)
    LinearLayout mPersonalGenderLl;
    @InjectView(R.id.ct_personal_ava_ll)
    View mPersonalAvaLl;


    @InjectView(R.id.ct_personal_phone_et)
    EditText mPersonalPhoneEt;
    @InjectView(R.id.ct_personal_email_et)
    EditText mPersonalEmailEt;
    @InjectView(R.id.ct_personal_identity_ll)
    View mPersonalIdentityV;
    @InjectView(R.id.ct_personal_identity_tv)
    TextView mPersonaIdentityTv;
    @InjectView(R.id.ct_selft_desc_v)
    View mPersonalDescV;
    @InjectView(R.id.ct_selft_desc_tv)
    TextView mPersonalDescTv;


    String mUploadedAvaUrl;
    UserInfo userInfo;
    AsyncHttpClient mClient = new AsyncHttpClient();

    //都在主线程上
    boolean mDisableEdit = false;
    boolean mIsUpLoading = false;

    boolean mNickAlertShown = false;
    boolean mRealnameAlertShown = false;
    boolean mGenderAlertShown = false;

    SweetAlertDialog mSubmitDialog;

    protected IImageUploader mImageUploader = new ServiceImageUploader(mClient, this);

    public HashMap<String, String> mGenderHashMap = new HashMap<>();

    public void initGenderMap() {
        mGenderHashMap.put(getString(R.string.ct_male), getString(R.string.ct_male_code));
        mGenderHashMap.put(getString(R.string.ct_male_code), getString(R.string.ct_male));

        mGenderHashMap.put(getString(R.string.ct_female), getString(R.string.ct_female_code));
        mGenderHashMap.put(getString(R.string.ct_female_code), getString(R.string.ct_female));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_profile);
        setContentView(R.layout.ct_my_profile);
        initGenderMap();
        ButterKnife.inject(this);
        requestUserInfo();
        userInfo = LoginInstance.getInstance(this).getUserInfo();
        setLocalValue();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_profile_submit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                submit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getGender(String code) {
        if (mGenderHashMap.containsKey(code)) {
            return mGenderHashMap.get(code);
        }
        return "";
    }

    public void setLocalValue() {
        ImageHelper.displayPersonImage(userInfo.getHeadPic(), mPersonalAvaIv, null);
        if (TextUtils.isEmpty(userInfo.getRealName())) {
            mPersonalNameEt.setEnabled(true);
        } else {
            mPersonalNameEt.setEnabled(false);
            mPersonalNameEt.setText(userInfo.getRealName());
        }
        mPersonalNickEt.setText(userInfo.getNick());
        mPersonalAvaNickTv.setText(userInfo.getNick());
        mPersonalAvaRegistTv.setText(Utils.getMsToD(userInfo.getGmtCreated()));
        mPersonalBirthEt.setText(userInfo.getBirthDay());

        mPersonalGenderTv.setText(getGender(userInfo.getGender()));

        mPersonalAreaEt.setText(userInfo.getCity());
        mPersonalJobEt.setText(userInfo.getCareer());

        mPersonalHobbyEt.setText(userInfo.getInterests());
        mPersonalLanguageEt.setText(userInfo.getLanguage());
        mPersonalSignEt.setText(userInfo.getSign());

        mPersonalPhoneEt.setEnabled(!TextUtils.isEmpty(userInfo.getMobile()));
        mPersonalPhoneEt.setText(userInfo.getMobile());


        mPersonalEmailEt.setEnabled(!TextUtils.isEmpty(userInfo.getEmail()));
        mPersonalEmailEt.setText(userInfo.getEmail());

        setOnClicks();
    }

    public void setRemotealue() {

        mPersonaIdentityTv.setText(getIndeneityString());


        if (getIndentityClickable()) {
            mPersonalIdentityV.setOnClickListener(this);
        }
    }


    public String getIndeneityString() {
        switch (userInfo.getIdCheckStatus()) {
            case UserInfo.ID_CHECK_ING:
                return getString(R.string.id_check_ing_msg);
            case UserInfo.ID_CHECK_SUC:
                return getString(R.string.id_check_suc_msg);
            case UserInfo.ID_CHECK_FAILED:
                return getString(R.string.id_check_failed_msg);
        }
        return "";
    }

    public boolean getIndentityClickable() {
        switch (userInfo.getIdCheckStatus()) {
            case UserInfo.ID_CHECK_ING:
                return false;
            case UserInfo.ID_CHECK_SUC:
                return false;
            case UserInfo.ID_CHECK_FAILED:
                return true;
        }
        return true;
    }

    public void setOnClicks() {
        //性别只能改一次
        if (TextUtils.isEmpty(getGender(userInfo.getGender()))) {
            mPersonalGenderLl.setOnClickListener(this);
            mPersonalGenderArrowV.setVisibility(View.VISIBLE);
        } else {
            mPersonalGenderArrowV.setVisibility(View.INVISIBLE);
        }
        mPersonalAvaLl.setOnClickListener(this);


        if (TextUtils.isEmpty(userInfo.getRealName())) {
            mPersonalNameEt.setOnFocusChangeListener(this);
        }
        mPersonalNickEt.setOnFocusChangeListener(this);
        mPersonalDescV.setOnClickListener(this);
        findViewById(R.id.ct_personal_identity_ll).setOnClickListener(this);
    }

    public void showGenderSelect() {
        if (mDisableEdit) {
            return;
        }
        final String[] values = {getString(R.string.ct_female), getString(R.string.ct_male)};
        new AlertDialog.Builder(this).setSingleChoiceItems(values, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPersonalGenderTv.setText(values[which]);
                        dialog.dismiss();
                        if (!mGenderAlertShown) {
                            showGenderEditAlert();
                            mGenderAlertShown = true;
                        }
                    }
                }).show();
    }

    public void showAvaSelect() {
        if (mIsUpLoading || mDisableEdit) {
            return;
        }

        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SelfHomePageActivity.isModifidy(requestCode, resultCode, data)) {
            requestUserInfo();
        }
        switch (requestCode) {
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap bp = GetImageHelper.getResizedBitmap(this, data.getData());
                    if (bp == null) {
                        MessageUtils.showToast(getString(R.string.select_image_failed));
                    } else {
                        updateImage(bp);
                        mPersonalAvaIv.setImageBitmap(bp);
                    }
                }
                break;
        }
    }

    protected void updateImage(final Bitmap bitmap) {
        mIsUpLoading = true;
        mImageUploader.upload(bitmap, new ImageUploadCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, String url) {
                mIsUpLoading = false;
                mUploadedAvaUrl = url;
                if (mDisableEdit) {
                    submit();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                mIsUpLoading = false;
                if (mDisableEdit) {
                    enableEdit(false, throwable.getMessage());
                } else {
                    MessageUtils.showToast(throwable.getMessage());
                }
            }

        });
    }

    public void disableEdit() {
        mDisableEdit = true;

        mPersonalNameEt.setEnabled(false);
        mPersonalNickEt.setEnabled(false);
        mPersonalGenderTv.setEnabled(false);
        mPersonalAreaEt.setEnabled(false);
        mPersonalJobEt.setEnabled(false);
        mPersonalHobbyEt.setEnabled(false);
        mPersonalLanguageEt.setEnabled(false);
        mPersonalSignEt.setEnabled(false);

        mSubmitDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mSubmitDialog.setTitleText(getString(R.string.ct_data_submiting));
        mSubmitDialog.setCancelable(false);
        mSubmitDialog.show();
    }

    public void enableEdit(boolean success, String msg) {
        mDisableEdit = false;

        mPersonalNameEt.setEnabled(true);
        mPersonalNickEt.setEnabled(true);
        mPersonalGenderTv.setEnabled(true);
        mPersonalAreaEt.setEnabled(true);
        mPersonalJobEt.setEnabled(true);
        mPersonalHobbyEt.setEnabled(true);
        mPersonalLanguageEt.setEnabled(true);
        mPersonalSignEt.setEnabled(true);
        if (mSubmitDialog != null) {
            if (success) {
                mSubmitDialog.setTitleText(getString(R.string.ct_submmit_suc))
                        .setConfirmText(getString(R.string.ct_i_know))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mSubmitDialog.dismissWithAnimation();
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            } else {
                mSubmitDialog.setTitleText(msg)
                        .setConfirmText(getString(R.string.ct_i_know))
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }
    }

    public void submit() {
        disableEdit();
        if (mIsUpLoading) {
            return;
        }
        final String email = mPersonalEmailEt.getText().toString();
        final String phone = mPersonalPhoneEt.getText().toString();
        final String birth = mPersonalBirthEt.getText().toString();
        final String ava = mUploadedAvaUrl;
        final String name = TextUtils.isEmpty(userInfo.getRealName()) ? mPersonalNameEt.getText().toString() : userInfo.getRealName();
        final String nick = mPersonalNickEt.getText().toString();
        final String gender = TextUtils.isEmpty(getGender(userInfo.getGender())) ? getGender(mPersonalGenderTv.getText().toString()) : userInfo.getGender();
        final String area = mPersonalAreaEt.getText().toString();
        final String language = mPersonalLanguageEt.getText().toString();
        final String job = mPersonalJobEt.getText().toString();
        final String hobby = mPersonalHobbyEt.getText().toString();
        final String sign = mPersonalSignEt.getText().toString();
        UserBusiness.updateProfile(this, mClient, new LabAsyncHttpResponseHandler() {
                    // file write async?
                    public void save() {
                        userInfo.setHeadPic(ava);
                        userInfo.setRealName(name);
                        userInfo.setNick(nick);
                        userInfo.setGender(gender);
                        userInfo.setCity(area);
                        userInfo.setLanguage(language);
                        userInfo.setCareer(job);
                        userInfo.setInterests(hobby);
                        userInfo.setSign(sign);
                        userInfo.setMobile(phone);
                        userInfo.setBirthDay(birth);
                        userInfo.setEmail(email);
                        LoginInstance.update(SelfActivity.this, userInfo);
                    }

                    @Override
                    public void onSuccess(LabResponse response, Object data) {
                        save();
                        enableEdit(true, "");
                    }


                    @Override
                    public void onFailure(LabResponse response, Object data) {
                        String errorMsg = response.msg;
                        if (TextUtils.isEmpty(errorMsg)) {
                            errorMsg = getString(R.string.update_failed);
                        }

                        enableEdit(false, errorMsg);
                    }

                }, ava, name, nick, gender, area,
                language, job, hobby, sign,
                phone, email, birth
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ct_selft_desc_v:
                SelfHomePageActivity.startForResult(this);
                break;
            case R.id.ct_personal_gender_ll:
                showGenderSelect();
                break;
            case R.id.ct_personal_ava_ll:
                showAvaSelect();
                break;
            case R.id.ct_personal_identity_ll:
                String[] pics = null;
                String pic1 = "";
                String pic2 = "";
                if (!TextUtils.isEmpty(userInfo.getIdPictures())) {
                    pics = userInfo.getIdPictures().split(",");
                    if (pics != null && pics.length > 2) {
                        pic1 = pics[0];
                        pic2 = pics[1];
                    }
                }
                SelfIdentificationActivity.start(this, userInfo.getIdRefuseReason(),
                        userInfo.getIdArea(), userInfo.getIdType(), userInfo.getGmtCreated(), pic1, pic2);
                break;
            default:
                break;
        }
    }


    public void showRealNameEditAlert() {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(getString(R.string.ct_profile_name_edit_alert, mPersonalNameEt.getText().toString()));
        pDialog.setConfirmText(getString(R.string.ct_i_know));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void showGenderEditAlert() {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(getString(R.string.ct_profile_gender_edit_alert, mPersonalGenderTv.getText().toString()));
        pDialog.setConfirmText(getString(R.string.ct_i_know));
        pDialog.setCancelable(false);
        pDialog.show();
    }


    public void showNickEditAlert() {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(getString(R.string.ct_profile_nick_edit_alert));
        pDialog.setConfirmText(getString(R.string.ct_i_know));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.ct_personal_name_et:
                if (!hasFocus && !mRealnameAlertShown) {
                    mRealnameAlertShown = true;
                    showRealNameEditAlert();
                }
                break;
            case R.id.ct_personal_nick_et:
                if (hasFocus && !mNickAlertShown) {
                    mNickAlertShown = true;
                    showNickEditAlert();
                }
                break;
            default:
                break;
        }
    }

    public void requestUserInfo() {
        showLoading();
        UserInfo info = LoginInstance.getInstance(this).getUserInfo();
        UserBusiness.getUserInfo(this, mClient, new LabAsyncHttpResponseHandler(UserInfo.class) {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null && data instanceof UserInfo) {
                    setRemotealue();
                    hideLoading();
                }

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
                hideLoading();
            }
        }, info.getUid());
        requestIntroduce();
    }

    public void requestIntroduce() {
        UserBusiness.getIntroduce(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                try {
                    JSONObject json = JSONObject.parseObject(data.toString());

                    Integer status = json.getInteger("introduceAuditStatus");
                    String reason = json.getString("introduceFailedReason");
                    final String desc = json.getString("introduce");
                    if (status == null) {
                        mPersonalDescV.setOnClickListener(SelfActivity.this);
                        return;
                    }
                    switch (status) {
                        case 0:
                            mPersonalDescTv.setText(getString(R.string.ct_homepage_status_ing));
                            mPersonalDescV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            break;
                        case 1:
                            mPersonalDescTv.setText(getString(R.string.ct_homepage_status_suc));
                            mPersonalDescV.setOnClickListener(SelfActivity.this);
                            break;
                        case 2:
                            mPersonalDescTv.setText(getString(R.string.ct_homepage_status_failed));
                            if (TextUtils.isEmpty(reason)) {
                                reason = getString(R.string.ct_homepage_status_failed);
                            }
                            mPersonalDescV.setOnClickListener(SelfActivity.this);
                            break;
                        default:
                            onUnExceptedError();
                            break;
                    }
                } catch (Exception e) {
                    onUnExceptedError();
                }
            }

            public void onUnExceptedError() {
                mPersonalDescTv.setText(getString(R.string.ct_fetch_failed));
                mPersonalDescV.setOnClickListener(SelfActivity.this);
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                String msg = response.msg;
                if (TextUtils.isEmpty(msg)) {
                    msg = getString(R.string.ct_fetch_failed);
                }
                mPersonalDescTv.setText(msg);
                mPersonalDescV.setOnClickListener(SelfActivity.this);
            }
        });
    }


}

