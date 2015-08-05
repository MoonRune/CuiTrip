package com.cuitrip.finder.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cuitrip.model.ServiceInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.location.LocationHelper;
import com.lab.utils.GetImageHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.SavedDescSharedPreferences;
import com.lab.utils.imageupload.URLImageParser;
import com.lab.widget.PicTextEditView;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;


/**
 * Created on 7/23.
 */
public class CreateServiceActivity extends BaseActivity {

    private static final String TAG = "CreateServiceActivity";
    private static final int REQUEST_IMAGE = 100;
    private static final int REQUEST_CREATE = 101;

    public static final String EDIT_MODE = "edit_mode";
    public static final String SERVICE_INFO = "service_info";
    public static final String LOCAL_SERVICE = "local_saved_service";

    private PicTextEditView mPicEditTextView;
    private TextView mTitle;

    private ServiceInfo mServiceInfo;
    private boolean mInEdit;
    private boolean mLocalService;

    private AsyncHttpClient mClient = new AsyncHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationHelper.getLoation(null);
        showActionBar(R.string.ct_add_service);
        Intent intent = getIntent();
        if (intent != null && (mInEdit = intent.getBooleanExtra(EDIT_MODE, false))) {
            mServiceInfo = (ServiceInfo) intent.getSerializableExtra(SERVICE_INFO);
            if (mServiceInfo == null) {
                MessageUtils.showToast(R.string.parameter_error);
                finish();
                return;
            }
            mLocalService = intent.getBooleanExtra(EDIT_MODE, false);
        }

        setContentView(R.layout.ct_finder_create_service);
        mTitle = (TextView)findViewById(R.id.service_name);
        mPicEditTextView = (PicTextEditView) findViewById(R.id.service_content);
        mPicEditTextView.setActivity(this);
        mPicEditTextView.setAsyncHttpClient(mClient);
        if (mInEdit) {
            mPicEditTextView.initDesc( URLImageParser.replae(mServiceInfo.getDescpt()), mServiceInfo.getBackPic());
            mTitle.setText(mServiceInfo.getName());
        } else {
            mPicEditTextView.initDesc("", null);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        LocationHelper.closeLocation();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_menu_create_service, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                if(TextUtils.isEmpty(mTitle.getText().toString().trim())){
                    MessageUtils.showToast(getString(R.string.ct_service_create_header_validate));
                    return true;
                }
                if(mPicEditTextView.isUpdating()){
                    MessageUtils.showToast(getString(R.string.ct_service_create_image_upload_validate));
                    return true;
                }
                String content = mPicEditTextView.generateDesc();
                if(TextUtils.isEmpty(content)){
                    MessageUtils.showToast(getString(R.string.ct_service_create_content_validate));
                    return true;
                }
                List<String> pictures = mPicEditTextView.getPictures();
                if (pictures == null || pictures.isEmpty()) {
                    MessageUtils.showToast(getString(R.string.ct_service_create_image_size_validate));
                    return true;
                }
                if(mServiceInfo == null){
                    mServiceInfo = new ServiceInfo();
                }
                mServiceInfo.setDescpt(content);
                mServiceInfo.setBackPic(mPicEditTextView.getMainPicture());
                mServiceInfo.setName(mTitle.getText().toString().trim());
                mServiceInfo.setPic(pictures);
                startActivityForResult(new Intent(this, CreateServiceOtherActivity.class)
                        .putExtra(CreateServiceActivity.SERVICE_INFO, mServiceInfo), REQUEST_CREATE);

                return true;
            case R.id.action_add_pic:
                mPicEditTextView.addImage(REQUEST_IMAGE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap bp = GetImageHelper.getResizedBitmap(this, data.getData());
                    if (bp == null) {
                        MessageUtils.showToast(getString(R.string.ct_create_image_failed_msg));
                    } else {
                        mPicEditTextView.insertImage(bp);
                    }
                }
                break;
            case REQUEST_CREATE:
                if (resultCode == RESULT_OK){
                    if(mLocalService){
                        SavedDescSharedPreferences.deleteServiceDesc(this);
                    }
                    finish();
                }
        }
    }

    public void onBackPressed() {
        final String desc = mPicEditTextView.generateDesc();
        if ((desc == null || TextUtils.isEmpty(desc.trim()) && !mInEdit)) {
            super.onBackPressed();
            return;
        }
        String[] tip;
        if(mServiceInfo != null && mServiceInfo.getSid() != null){
            tip = new String[]{getString(R.string.ct_edit_give_up), getString(R.string.ct_cancel)};
        }else{
            tip = new String[]{getString(R.string.ct_edit_give_up), getString(R.string.ct_close_and_save), getString(R.string.ct_cancel)};
        }
        AlertDialog.Builder builder = MessageUtils.createHoloBuilder(this);
        builder.setAdapter(new ArrayAdapter<String>(this, R.layout.ct_choice_item, R.id.checktext,
                tip), new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    finish();
                } else if (i == 1) {
                    if(mServiceInfo != null && mServiceInfo.getSid() != null){
                        //do nothig
                    }else{
                        SavedDescSharedPreferences.saveServiceDesc(CreateServiceActivity.this,
                                mTitle.getText().toString(), desc, mPicEditTextView.getMainPicture());
                        mPicEditTextView.deletePictures();
                        finish();
                    }
                } else {
                    //do nothing
                }
            }
        });
        builder.create().show();
    }
}
