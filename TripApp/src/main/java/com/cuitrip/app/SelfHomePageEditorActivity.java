package com.cuitrip.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.cuitrip.business.UserBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.GetImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.imageupload.IImageUploader;
import com.lab.utils.imageupload.ImageUploadCallback;
import com.lab.utils.imageupload.URLImageParser;
import com.lab.utils.imageupload.imp.ServiceImageUploader;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created bqf on 7/16.
 * <p/>
 * 1.渲染数据（本地取>从homepage页传来 ）,创建span 赋予edittext，并启动未上传成功的图片的上传
 * 2.点击添加图片，创建span图片，赋予edittext，并启动上传，上传成功替换span，若此时已经提交ing并且所有图片上传成功，提交
 * 3.点击提交,验证是否超过{CONTENT_SIE_LIMITION}，如果验证通过，尝试提交锁定添加图片|提交|文本编辑，如果图片未上传，等待图片上传成功后自动提交，如果已经上传完毕，直接提交
 */
public class SelfHomePageEditorActivity extends BaseActivity {

    private static final int CONTENT_SIE_LIMITION = 500;

    public static final String TAG = "SelfHomePageEditorActivity";
    public static final String BITMAP_KEY = "<bit_map>";
    public static final String BITMAP_END = "</bit_map>";

    public static final String BITMAP_REMOTE_KEY = "<div><img src=\"";
    public static final String BITMAP_REMOTE_END = "\" width=\"100%\" /></div>";
    private static final int REQUEST_IMAGE = 100;

    public static final String CONTENT_KEY = "CONTENT_KEY";
    public static final int REQUEST_HOME_PAGE_UPDATE = 9999;
    public static final int HOME_PAGE_UPDATED = 1000;
    private HashMap<Bitmap, UrlTaskSpannable> mBitmapUrl = new HashMap<>();

    @InjectView(R.id.ct_personal_desc_et)
    public EditText mContentEt;

    private AsyncHttpClient mClient = new AsyncHttpClient();
    protected IImageUploader mImageUploader = new ServiceImageUploader(mClient, this);

    //submition starts after image uploaded  .
    protected AtomicBoolean isSubmitting = new AtomicBoolean(false);

    public static boolean isEdited(int requestCode, int resultCode, Intent data) {
        return requestCode == REQUEST_HOME_PAGE_UPDATE && resultCode == HOME_PAGE_UPDATED;
    }

    public static void startActivity(Activity context, String content) {
        Intent intent = new Intent(context, SelfHomePageEditorActivity.class);
        intent.putExtra(CONTENT_KEY, content);
        context.startActivityForResult(intent, REQUEST_HOME_PAGE_UPDATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ct_homepage_submit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isSubmitting.get()) {
            switch (item.getItemId()) {
                case R.id.action_submit:
                    trySubmit();
                    break;
                case R.id.action_add_image:
                    injectImage();
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.ct_homepage_eidt);
        setContentView(R.layout.ct_my_home_editor);
        ButterKnife.inject(this);
        loadContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.cancelAllRequests(true);
        for (Bitmap bitmap : mBitmapUrl.keySet()) {
            bitmap.recycle();
        }
        mBitmapUrl.clear();
    }

    protected void loadContent() {
        final SweetAlertDialog loadDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        loadDialog.setTitleText(getString(R.string.loading_text));
        loadDialog.setCancelable(false);
        loadDialog.show();
        mContentEt.setEnabled(false);
        new AsyncTask() {
            HashMap<String, Bitmap> localBitmaps = new HashMap<>();
            HashMap<String, Bitmap> remoteBitmaps = new HashMap<>();
            String content;

            @Override
            protected Object doInBackground(Object[] params) {
                content = getIntent().getStringExtra(CONTENT_KEY);
                if (TextUtils.isEmpty(content)) {
                    content = UserConfig.getInstance().getPersonalDesc();
                }
                if (TextUtils.isEmpty(content)) {
                    content = URLImageParser.replae(content);
                    content = URLImageParser.replaeWidth(content);
                }
                LogHelper.e(TAG, "content " + content);
                loopSearchUnUploaded(content, localBitmaps);
                loopSearchUploaded(content, remoteBitmaps);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mContentEt.setEnabled(true);
                loadDialog.dismissWithAnimation();
                buildBitmapSpannable(content, localBitmaps, remoteBitmaps);
            }
        }.execute();
    }

    public List<Bitmap> getUnUploadedBitmaps() {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (Bitmap bitmap : mBitmapUrl.keySet()) {
            if (TextUtils.isEmpty(mBitmapUrl.get(bitmap).getUrl())) {
                bitmaps.add(bitmap);
            }
        }
        return bitmaps;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveContent();
    }

    //if not uploaded //save bitmap as hashcode
    protected void saveContent() {
        if (isAllBitMapUploaded()) {
            PersonalDescSaver.saveAll(null,
                    mContentEt.getText().toString());
        } else {
            PersonalDescSaver.saveAll(getUnUploadedBitmaps(),
                    mContentEt.getText().toString());

        }
    }

    protected void injectImage() {
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
                        insertBitmapTempSpan(bp);
                    }
                }
                break;
        }
    }

    protected void updateImage(final Bitmap bitmap) {
        mImageUploader.upload(bitmap, new ImageUploadCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, String url) {
                replaceBitmapTempSpanWithRemote(bitmap, url);
                if (isSubmitting.get()) {
                    secondTrySubmit();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                MessageUtils.showToast(throwable.getMessage());
                removeBitmapSpan(bitmap);
                if (isSubmitting.get()) {
                    stopSubmitProgressing(false, getString(R.string.ct_upload_failed));
                }
            }
        });
    }

    public String buildBitmapKey(Bitmap bitmap) {
        return buildBitmapKey(String.valueOf(bitmap.hashCode()));
    }

    public String buildBitmapKey(String key) {
        return BITMAP_KEY + key + BITMAP_END;
    }


    public String buildBitmapRemoteKey(String key) {
        return BITMAP_REMOTE_KEY + key + BITMAP_REMOTE_END;
    }

    public void removeBitmapSpan(Bitmap bitmap) {
        String bitmapKey = buildBitmapKey(bitmap);
        int position = mContentEt.getText().toString().indexOf(bitmapKey);
        mContentEt.getText().delete(position, position + bitmapKey.length());

    }

    public String buildImageString(String url) {
        return BITMAP_REMOTE_KEY + url + BITMAP_REMOTE_END;
    }

    //插入 临时span bitmap在内存
    protected void insertBitmapTempSpan(Bitmap bitmap) {
        Drawable drawable = buildDrawable(bitmap);
        ImageSpan imgSpan = buildSpan(drawable);
        String name = buildBitmapKey(bitmap);
        SpannableString spanString = new SpannableString("\n" + name + "\n");
        spanString.setSpan(imgSpan, 1, spanString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int index = mContentEt.getSelectionStart();
        Editable edit = mContentEt.getEditableText();
        if (index < 0 || index >= edit.length()) {
            edit.append(spanString);
        } else {
            edit.insert(index, spanString);
        }
        mBitmapUrl.put(bitmap, new UrlTaskSpannable(name));
        updateImage(bitmap);
        resetSelection();
    }

    //插入远程span , bitmap从远程获取 ，将span BITMAP_KEY ＋url+BITMAP_END 替换为 BITMAP_REMOTE_KEY ＋url+BITMAP_REMOTE_END
    public void replaceBitmapTempSpanWithRemote(Bitmap bitmap, String url) {
        if (mBitmapUrl.get(bitmap) != null) {
            UrlTaskSpannable record = mBitmapUrl.get(bitmap);
            record.setUrl(url);
            String insertString = buildImageString(url);
            String oldName = record.getNowName();
            ImageSpan imgSpan = buildSpan(buildDrawable(bitmap));
            SpannableString spanString = new SpannableString(insertString);
            spanString.setSpan(imgSpan, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int position = mContentEt.getText().toString().indexOf(oldName);
            mContentEt.getText().replace(position, position + oldName.length(), spanString);
        } else {
            //  interface throw exception in stage | record in prod
        }
    }

    //插入bitmap ，由于bitmap本地存储时压缩，所以要替换名字
    protected void replaceBitmapspanWithRemote(SpannableStringBuilder spannableStringBuilder, String name, Bitmap bitmap) {
        SpannableString spanString = null;
        String oldName = buildBitmapRemoteKey(name);
        if (bitmap != null) {
            Drawable drawable = buildDrawable(bitmap);
            ImageSpan imgSpan = buildSpan(drawable);
            spanString = new SpannableString(oldName);
            spanString.setSpan(imgSpan, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spanString = new SpannableString("");
        }
        int position = spannableStringBuilder.toString().indexOf(oldName);
        if (position >= 0) {
            spannableStringBuilder.replace(position, position + oldName.length(),
                    spanString);
        }
        LogHelper.e(TAG, "after replace " + spannableStringBuilder.toString());
    }

    protected void replaceBitmapSpanWithLocal(SpannableStringBuilder spannableStringBuilder, String name, Bitmap bitmap) {
        SpannableString spanString = null;
        String oldName = buildBitmapKey(name);

        if (bitmap != null) {
            Drawable drawable = buildDrawable(bitmap);
            ImageSpan imgSpan = buildSpan(drawable);
            spanString = new SpannableString(buildBitmapKey(bitmap));
            spanString.setSpan(imgSpan, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spanString = new SpannableString("");
        }
        int position = spannableStringBuilder.toString().indexOf(oldName);
        if (position >= 0) {
            spannableStringBuilder.replace(position, position + oldName.length(),
                    spanString);
            mBitmapUrl.put(bitmap, new UrlTaskSpannable(spanString.toString()));
            updateImage(bitmap);
        }
        LogHelper.e(TAG, "after replace " + spannableStringBuilder.toString());
    }

    //换行 居中 如果不设置padding 可能会导致部分机型图片显示2次！！ 囧 research
    protected Drawable buildDrawable(Bitmap bitmap) {
        Drawable drawable =
                new BitmapDrawable(getResources(), bitmap);
        int tempWidth = bitmap.getWidth();
        int height = bitmap.getHeight();
        int width = MainApplication.getInstance().getPageWidth();
        float leftPadding = getResources().getDimension(R.dimen.ct_personal_desc_left_padding);
        float topPadding = getResources().getDimension(R.dimen.ct_personal_desc_top_padding);
        width -= leftPadding;
        height = width * height / tempWidth;
        drawable.setBounds((int) leftPadding, (int) topPadding, (width > 0 ? width : 0) - (int) leftPadding * 2, (height > 0 ? height : 0) + (int) topPadding * 2);
        return drawable;
    }

    protected ImageSpan buildSpan(Drawable drawable) {
        return new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
    }


    protected void resetSelection() {
        mContentEt.setSelection(mContentEt.getText().length());
    }

    public void trySubmit() {
        String validate = validateSubmit();
        if (TextUtils.isEmpty(validate)) {
            showSubmitProgressing();
            if (isAllBitMapUploaded()) {
                submit();
            }
        } else {
            SweetAlertDialog loadDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            loadDialog.setTitleText(validate);
            loadDialog.setConfirmText(getString(R.string.ct_i_know));
            loadDialog.setCancelable(false);
            loadDialog.show();
        }
    }

    //search spans for none start with BITMAP_KEY
    protected boolean isAllBitMapUploaded() {
        return !mContentEt.getText().toString().contains(BITMAP_KEY);
    }

    protected boolean isAllBitMapUploaded(String text) {
        return !text.contains(BITMAP_KEY);
    }

    protected void buildBitmapSpannable(String text, HashMap<String, Bitmap> bitmapNameMap, HashMap<String, Bitmap> remoteBitmapMaps) {


        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        if (bitmapNameMap != null) {
            for (String name : bitmapNameMap.keySet()) {
                replaceBitmapSpanWithLocal(spannableStringBuilder, name, bitmapNameMap.get(name));
            }
        }
        if (remoteBitmapMaps != null) {
            for (String name : remoteBitmapMaps.keySet()) {
                replaceBitmapspanWithRemote(spannableStringBuilder, name, remoteBitmapMaps.get(name));
            }
        }
        mContentEt.setText(spannableStringBuilder);
        resetSelection();
        LogHelper.e(TAG, "result " + spannableStringBuilder.toString());
    }


    protected HashMap<String, Bitmap> loopSearchUploaded(String loopText, HashMap<String, Bitmap> bitmaps) {
        if (TextUtils.isEmpty(loopText.trim())) {
            return bitmaps;
        }
        int start = loopText.indexOf(BITMAP_REMOTE_KEY);
        int end = loopText.indexOf(BITMAP_REMOTE_END);
        if (start < 0 || end < 0 || end < start) {
            return bitmaps;
        }
        String name = loopText.substring(start + BITMAP_REMOTE_KEY.length(), end);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(name);
        bitmaps.put(name, bitmap);
        return loopSearchUploaded(loopText.substring(end + BITMAP_REMOTE_END.length(), loopText.length()), bitmaps);

    }

    protected HashMap<String, Bitmap> loopSearchUnUploaded(String loopText, HashMap<String, Bitmap> bitmaps) {
        if (TextUtils.isEmpty(loopText.trim())) {
            return bitmaps;
        }
        int start = loopText.indexOf(BITMAP_KEY);
        int end = loopText.indexOf(BITMAP_END);
        if (start < 0 || end < 0 || end < start) {
            return bitmaps;
        }
        String name = loopText.substring(start + BITMAP_KEY.length(), end);
        Bitmap bitmap = PersonalDescSaver.getBitmap(name);
        bitmaps.put(name, bitmap);
        return loopSearchUnUploaded(loopText.substring(end + BITMAP_END.length(), loopText.length()), bitmaps);
    }

    SweetAlertDialog mSubmitDialog;

    protected void showSubmitProgressing() {
        isSubmitting.set(true);
        mContentEt.setEnabled(false);

        if (mSubmitDialog == null) {
            mSubmitDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mSubmitDialog.setTitleText(getString(R.string.ct_data_submiting));
            mSubmitDialog.setCancelable(false);
        } else {
            mSubmitDialog.setTitleText(getString(R.string.ct_data_submiting)).
                    setConfirmText("").
                    changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        }
        mSubmitDialog.show();

    }

    protected void stopSubmitProgressing(boolean success, String msg) {
        isSubmitting.set(false);
        mContentEt.setEnabled(true);
        if (mSubmitDialog != null) {
            if (success) {
                mSubmitDialog.setTitleText(getString(R.string.ct_submmit_suc))
                        .setConfirmText(getString(R.string.ct_i_know))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mContentEt.setText("");
                                mSubmitDialog.dismissWithAnimation();
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            } else {
                mSubmitDialog.setTitleText(msg)
                        .setConfirmText(getString(R.string.ct_i_know))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mSubmitDialog.dismissWithAnimation();
                                mSubmitDialog = null;
                            }
                        })
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }
    }

    protected void secondTrySubmit() {
        if (isAllBitMapUploaded()) {
            String validate = validateSubmit();
            if (TextUtils.isEmpty(validate)) {
                submit();
            } else {
                stopSubmitProgressing(false, validate);
            }
        }
    }

    protected String validateSubmit() {
        //validate limitation
        if (mContentEt.getText().toString().length() <= CONTENT_SIE_LIMITION) {
            return "";
        } else {
            return getString(R.string.ct_personal_desc_limit_msg, CONTENT_SIE_LIMITION);
        }
    }

    protected void submit() {
        showSubmitProgressing();
        UserInfo userInfo = LoginInstance.getInstance(this).getUserInfo();
        String content= mContentEt.getText().toString();
        LogHelper.e("omg","before bad replace :"+content);
        LogHelper.e("omg","after bad replace :"+content);
        LogHelper.e(TAG, "submit  : " + userInfo.getUid() + "|" + userInfo.getToken() + "|" + content);
        UserBusiness.updateIntroduce(this, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                MessageUtils.showToast(R.string.ct_home_page_update_posted);
                PersonalDescSaver.clean();
                setResult(HOME_PAGE_UPDATED);
                stopSubmitProgressing(true, "");
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                LogHelper.e(TAG, "submit failed : " + data + "|" + response.code + "|" + response.result);
                String msg = response.msg;
                if (TextUtils.isEmpty(msg)) {
                    msg = getString(R.string.ct_upload_failed);
                }
                stopSubmitProgressing(false, msg);

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        }, userInfo.getUid(), userInfo.getToken(),content);
    }

    public class UrlTaskSpannable {
        private String url;
        private String nowName;


        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNowName() {
            return nowName;
        }

        public UrlTaskSpannable(String oldName) {
            this.nowName = oldName;
        }

    }
}
