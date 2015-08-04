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

import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.GetImageHelper;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;
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

    @InjectView(R.id.ct_personal_ava_iv)
    CircleImageView mPersonalAvaIv;
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
    LinearLayout mPersonalAvaLl;


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
        userInfo = LoginInstance.getInstance(this).getUserInfo();
        setLocalValue();
        setOnClicks();

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
        userInfo = LoginInstance.getInstance(this).getUserInfo();
        ImageHelper.displayPersonImage(userInfo.getHeadPic(), mPersonalAvaIv, null);
        if (TextUtils.isEmpty(userInfo.getRealName())) {
            mPersonalNameEt.setEnabled(true);
        } else {
            mPersonalNameEt.setEnabled(false);
            mPersonalNameEt.setText(userInfo.getRealName());
        }
        mPersonalNickEt.setText(userInfo.getNick());

        mPersonalGenderTv.setText(getGender(userInfo.getGender()));

        mPersonalAreaEt.setText(userInfo.getCity());
        mPersonalJobEt.setText(userInfo.getCareer());
        mPersonalHobbyEt.setText(userInfo.getInterests());
        mPersonalLanguageEt.setText(userInfo.getLanguage());
        mPersonalSignEt.setText(userInfo.getSign());
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
                language, job, hobby, sign
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ct_personal_gender_ll:
                showGenderSelect();
                break;
            case R.id.ct_personal_ava_ll:
                showAvaSelect();
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
}

