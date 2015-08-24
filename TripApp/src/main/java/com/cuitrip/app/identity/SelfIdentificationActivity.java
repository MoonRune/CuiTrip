package com.cuitrip.app.identity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.business.UserBusiness;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
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

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.CountryPage;

/**
 * Created on 7/16.
 */
public class SelfIdentificationActivity extends BaseActivity implements CountryPage.OnResult {
    public static final String MODE_KEY = "MODE_KEY";
    @InjectView(R.id.ct_not_pass_tv)
    TextView ctNotPassTv;
    @InjectView(R.id.ct_user_country_tv)
    TextView ctUserCountryTv;
    @InjectView(R.id.ct_user_identity_type_tv)
    TextView ctUserIdentityTypeTv;
    @InjectView(R.id.ct_user_validate_date_tv)
    TextView ctUserValidateDateTv;
    @InjectView(R.id.ct_identity_one)
    ImageView ctIdentityOne;
    @InjectView(R.id.ct_identity_two)
    ImageView ctIdentityTwo;
    @InjectView(R.id.ct_identity_ad)
    RelativeLayout ctIdentityAd;
    @InjectView(R.id.re_upload)
    TextView reUpload;

    IdentityMode identityMode;
    private static final int REQUEST_IMAGE = 100;
    private static final int REQUEST_IDENTITY = 102;

    AsyncHttpClient mClient = new AsyncHttpClient();
    protected IImageUploader mImageUploader = new ServiceImageUploader(mClient, this);
    boolean isSubmit = false;

    public static void start(Activity context, String feailedReason, String country, String type, String date, String pic1, String pic2) {
        ArrayList<ImageBitmap> bitmaps = new ArrayList<>();
        if (!TextUtils.isEmpty(pic1)) {
            ImageBitmap imageBitmap = new ImageBitmap();
            imageBitmap.setUrl(pic1);
            bitmaps.add(imageBitmap);
        }
        if (!TextUtils.isEmpty(pic2)) {
            ImageBitmap imageBitmap = new ImageBitmap();
            imageBitmap.setUrl(pic2);
            bitmaps.add(imageBitmap);
        }
        IdentityMode indentityMode = new IdentityMode(feailedReason, country, type, date, bitmaps);
        context.startActivityForResult(new Intent(context, SelfIdentificationActivity.class)
                .putExtra(MODE_KEY, indentityMode), REQUEST_IDENTITY);
    }

    public static boolean isModifyed(int request, int response, Intent date) {
        return request == REQUEST_IDENTITY && response == RESULT_OK;
    }

    @OnClick(R.id.ct_not_pass_tv)
    public void hideReson() {
        ctNotPassTv.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_OK:
                trySubmit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void trySubmit() {
        if (TextUtils.isEmpty(identityMode.getCountry())) {
            MessageUtils.showToast("请设置国家");
            return;
        }
        if (TextUtils.isEmpty(identityMode.getIdentityType())) {
            MessageUtils.showToast("请设置证件类型");
            return;
        }
        if (TextUtils.isEmpty(identityMode.getIdentityDate())) {
            MessageUtils.showToast("请设置有效日期");
            return;
        }

        if (identityMode.getImages().size() < 2) {
            MessageUtils.showToast("请上传2张照片");
            return;
        }
        submit();

    }

    public void submit() {
        showNoCancelDialog();
        isSubmit = true;
        if (identityMode.isImageUploaded()) {
            UserBusiness.updateIndentity(this, mClient, new LabAsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(LabResponse response, Object data) {
                            hideNoCancelDialog();
                            setResult(RESULT_OK);
                            MessageUtils.showToast(getString(R.string.ct_upload_indeitiy_suc));
                            finish();
                            isSubmit = false;
                        }

                        @Override
                        public void onFailure(LabResponse response, Object data) {
                            isSubmit = false;
                            hideNoCancelDialog();
                            String msg ;
                            if (response !=null &&!TextUtils.isEmpty(response.msg)){
                                msg = response.msg;
                            }else {
                                msg= PlatformUtil.getInstance().getString(R.string.data_error);
                            }
                            MessageUtils.showToast(msg);

                        }
                    }, identityMode.getImagesString(),
                    identityMode.getIdentityType(),
                    identityMode.getCountry(),
                    identityMode.getIdentityDate());
        } else {
            //wait upload thread
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_id);
        setContentView(R.layout.ct_my_id);
        ButterKnife.inject(this);
        identityMode = (IdentityMode) getIntent().getSerializableExtra(MODE_KEY);
        render(identityMode);
        ctUserValidateDateTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                identityMode.setIdentityDate(ctUserValidateDateTv.getText().toString());
            }

        });
    }

    @OnLongClick(R.id.ct_identity_one)
    public boolean removeBitmapOne() {
        MessageUtils.createHoloBuilder(this).setTitle("确定要删除该图片吗").setPositiveButton(R.string.ct_confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeBitmap(0);
                    }
                }).setNegativeButton(R.string.ct_cancel, null).show();
        return true;
    }
    @OnClick(R.id.ct_user_validate_date_ll)
    public void showDate(){

      MessageUtils.showDateCheck(this, new MessageUtils.DateCheckListener() {
          @Override
          public void onDataSelect(String s) {
              ctUserValidateDateTv.setText(s);
          }
      });
    }

    @OnClick(R.id.ct_user_country_v)
    public void showCountry() {

        CountryPage countryPage = new CountryPage();
        countryPage.setOnResultListener(this);
        countryPage.showForResult(this, null);
    }

    @OnLongClick(R.id.ct_identity_two)
    public boolean removeBitmapTwo() {
        MessageUtils.createHoloBuilder(this).setTitle("确定要删除该图片吗").setPositiveButton(R.string.ct_confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeBitmap(1);
                    }
                }).setNegativeButton(R.string.ct_cancel, null).show();
        return true;
    }

    public void removeBitmap(int index) {
        if (identityMode.getImages().size() > index) {
            identityMode.getImages().remove(index);
            render(identityMode);
        }
    }

    @OnClick(R.id.re_upload)
    public void removeBitmapAll() {
        identityMode.getImages().clear();
        render(identityMode);
        addBitmap();
    }

    @OnClick(R.id.ct_identity_ad)
    public void addBitmap() {
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
                        ImageBitmap imageBitmap = new ImageBitmap();
                        imageBitmap.setBitmap(bp);
                        identityMode.getImages().add(imageBitmap);
                        uploadBitmap(imageBitmap);
                        render(identityMode);
                    }
                }
                break;
        }
    }

    public void uploadBitmap(final ImageBitmap imageBitmap) {
        mImageUploader.upload(imageBitmap.getBitmap(), new ImageUploadCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, String url) {
                imageBitmap.setUrl(url);
                if (isSubmit) {
                    trySubmit();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                MessageUtils.showToast(getString(R.string.ct_upload_image_failed));
                identityMode.getImages().remove(imageBitmap);
                render(identityMode);
                if (isSubmit) {
                    hideNoCancelDialog();
                    isSubmit = false;
                }
            }
        });
    }

    public String getIdentityType(String code) {
        return UnitUtils.getIndentityName(code);
    }

    public void setImage(ImageView view, ImageBitmap imageBitmap) {
        if (TextUtils.isEmpty(imageBitmap.getUrl())) {
            view.setImageBitmap(imageBitmap.getBitmap());
        } else {
            ImageHelper.displayCtImage(imageBitmap.getUrl(), view, null);
        }
    }

    @OnClick(R.id.ct_user_identity_type_ll)
    public void showIdentityType() {
        AlertDialog.Builder builder = MessageUtils.createHoloBuilder(this);
        builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
                getResources().getStringArray(R.array.ct_identity_types)), new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i < getResources().getStringArray(R.array.ct_identity_types).length) {
                    identityMode.setIdentity(
                            UnitUtils.getIndentityCode(
                                    getResources().getStringArray(R.array.ct_identity_types)[i])
                    );
                    render(identityMode);
                }
            }
        });
        AlertDialog dialog = builder.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = getResources().getDimensionPixelOffset(R.dimen.ct_dp_240); // 高度
        window.setAttributes(lp);
    }

    protected void render(IdentityMode identityMode) {
        if (TextUtils.isEmpty(identityMode.getFailedReaon())) {
            ctNotPassTv.setVisibility(View.GONE);
        } else {
            ctNotPassTv.setVisibility(View.VISIBLE);
            ctNotPassTv.setText(identityMode.getFailedReaon());
        }

        ctUserCountryTv.setText(identityMode.getCountry());
        ctUserIdentityTypeTv.setText(getIdentityType(identityMode.getIdentityType()));
        ctUserValidateDateTv.setText(identityMode.getIdentityDate());

        switch (identityMode.getImages().size()) {
            case 0:
                ctIdentityOne.setVisibility(View.GONE);
                ctIdentityTwo.setVisibility(View.GONE);
                ctIdentityAd.setVisibility(View.VISIBLE);
                reUpload.setVisibility(View.GONE);
                break;
            case 1:
                ctIdentityOne.setVisibility(View.VISIBLE);
                setImage(ctIdentityOne, identityMode.getImages().get(0));
                ctIdentityTwo.setVisibility(View.GONE);
                ctIdentityAd.setVisibility(View.VISIBLE);
                reUpload.setVisibility(View.GONE);
                break;
            default:
                ctIdentityOne.setVisibility(View.VISIBLE);
                setImage(ctIdentityOne, identityMode.getImages().get(0));
                ctIdentityTwo.setVisibility(View.VISIBLE);
                setImage(ctIdentityTwo, identityMode.getImages().get(1));
                ctIdentityAd.setVisibility(View.GONE);
                reUpload.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onResult(HashMap<String, Object> data) {
        if (data != null) {
            int page = (Integer) data.get("page");
            if (page == 1) {
                // 国家列表返回
                String currentId = (String) data.get("id");
                if (!TextUtils.isEmpty(currentId)) {
//                HashMap<String, String>  countryRules = (HashMap<String, String>) data.get("rules");
                    String[] country = SMSSDK.getCountry(currentId);
                    if (country != null) {
                        ctUserCountryTv.setText(country[0]);
                        identityMode.setCountry(country[0]);
                    }
                }
            }
        }
    }
}
