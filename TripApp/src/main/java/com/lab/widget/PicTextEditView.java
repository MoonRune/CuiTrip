package com.lab.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.service.R;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.Constants;
import com.lab.utils.GetImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 7/25.
 */
public class PicTextEditView extends LinearLayout implements ImageSelectView.OnImageClickdCallback {
    public static final String TAG = "PicTextEditView";

    private TextEditSplit mCurrentEdit;
    private int mPositionInEdit;
    private int mPositionInParent;
    private AsyncHttpClient mClient;
    private Activity mActivity;
    private List<String> mPictures = new ArrayList<String>();
    private String mMainPicture;

    private List<String> mUrlsToDeleted = new ArrayList<String>();

    public PicTextEditView(Context context) {
        super(context);
        init();
    }

    public PicTextEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PicTextEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    public void setAsyncHttpClient(AsyncHttpClient client) {
        mClient = client;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void initDesc(String desc, String mainPic) {
        if (TextUtils.isEmpty(desc)) {
            TextEditSplit textEditSplit = new  TextEditSplit(getContext());
            textEditSplit.setHint(R.string.ct_create_service_hint);
            textEditSplit.setHintTextColor(getContext().getResources().getColor(R.color.ct_text_hint));
            addView(textEditSplit);
        } else {
            generateView(desc, mainPic);
        }
    }

    private void generateView(String desc, String mainPic) {
        Pattern pattern = Pattern.compile(Constants.IMAGE_PATTERN);
        Matcher matcher = pattern.matcher(desc);
        int index = 0;
        while (matcher.find()) {
            int start = matcher.start();
            String temp = desc.substring(index, start);
            index = matcher.end();
            if (!TextUtils.isEmpty(temp)) {
                TextEditSplit editSplit = new TextEditSplit(mActivity);
                editSplit.setText(temp);
                addView(editSplit);
            }
            String url = matcher.group(1);
            LogHelper.d(TAG, "url: " + url);
            if (!TextUtils.isEmpty(url)) {
                ImageSelectView view = new ImageSelectView(mActivity);
                view.setRemoteUrl(url);
                if (url.equals(mainPic)) {
                    view.setSelected(true);
                }
                view.setOnImageClickdCallback(this);
                if (getChildAt(getChildCount() - 1) instanceof ImageSelectView) {
                    addView(new TextEditSplit(mActivity));
                }
                addView(view);
            }
        }
        if(index < desc.length() - 1){
            TextEditSplit editSplit = new TextEditSplit(mActivity);
            editSplit.setText(desc.substring(index, desc.length()));
            addView(editSplit);
        }
        int childCount = getChildCount();
        if (childCount == 0 || getChildAt(0) instanceof ImageSelectView) {
            addView(new TextEditSplit(mActivity), 0);
        }
        childCount = getChildCount();
        if (childCount > 0 && getChildAt(childCount - 1) instanceof ImageSelectView) {
            addView(new TextEditSplit(mActivity), childCount);
        }
    }

    public String generateDesc() {
        mPictures.clear();
        mMainPicture = null;
        StringBuilder sb = new StringBuilder();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof TextEditSplit) {
                TextEditSplit textEditSplit = (TextEditSplit) child;
                String content = textEditSplit.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    sb.append(content);
                }
            } else if (child instanceof ImageSelectView) {
                ImageSelectView imageSelectView = (ImageSelectView) child;
                if (!TextUtils.isEmpty(imageSelectView.getRemoteUrl())) {
                    sb.append(Constants.IMAGE_URL_HEAD)
                            .append(imageSelectView.getRemoteUrl())
                            .append(Constants.IMAGE_URL_END);
                    mPictures.add(imageSelectView.getRemoteUrl());
                    if (mMainPicture == null) {
                        mMainPicture = imageSelectView.getRemoteUrl();//默认第一张为主图
                    }
                    if (imageSelectView.isSelected()) {
                        mMainPicture = imageSelectView.getRemoteUrl();
                    }
                }
            }
        }
        return sb.toString();
    }

    public List<String> getPictures() {
        return mPictures;
    }

    public String getMainPicture() {
        if (mMainPicture != null) {
            return mMainPicture;
        } else if (!mPictures.isEmpty()) {
            return mPictures.get(0);
        } else {
            return null;
        }
    }

    public boolean isUpdating() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof ImageSelectView) {
                if (((ImageSelectView) getChildAt(i)).isUpdating()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addImage(int requestCode) {
        if (mActivity == null) {
            return;
        }
        int childCount = getChildCount();
        int picCount = 0;
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof ImageSelectView) {
                ++picCount;
            }
        }
        if (picCount >= 9) {
            MessageUtils.createHoloBuilder(mActivity).setMessage(R.string.ct_at_most_image_size_nine_limition)
                    .setPositiveButton(R.string.ct_i_know, null).show();
            return;
        }

        View foucs = findFocus();
        if (foucs == null || !(foucs instanceof TextEditSplit)) {
            MessageUtils.showToast(R.string.ct_select_pic_poition);
            return;
        }
        mCurrentEdit = (TextEditSplit) foucs;
        mPositionInParent = indexOfChild(mCurrentEdit);
        mPositionInEdit = mCurrentEdit.getSelectionStart();
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        mActivity.startActivityForResult(photoPickerIntent, requestCode);
    }

    private void updateImage(final ImageSelectView view, String base64) {
        view.setUpdating(true);
        ServiceBusiness.upPic(mActivity, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                view.setUpdating(false);
                try {
                    JSONObject json = JSONObject.parseObject(data.toString());
                    String url = json.getString("picurl");
                    if (TextUtils.isEmpty(url)) {
                        MessageUtils.showToast(R.string.ct_upload_failed);
                        removeImageView(view);
                    } else {
                        view.setRemoteUrl(url);
                    }
                } catch (Exception e) {
                    LogHelper.e(TAG, "update pic: " + e);
                    e.printStackTrace();
                    removeImageView(view);
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                view.setUpdating(false);
                if (!TextUtils.isEmpty(response.msg)) {
                    MessageUtils.showToast(response.msg);
                } else {
                    MessageUtils.showToast(R.string.ct_upload_failed);
                }
                removeImageView(view);
            }
        }, base64);
    }

    public void insertImage(Bitmap bp) {
        ImageSelectView view = new ImageSelectView(getContext());
        view.setOnImageClickdCallback(this);
        updateImage(view, GetImageHelper.bitmapToBase64String(bp));
        view.setImageBitmap(bp);

        int childCount = getChildCount();
        boolean firstImage = true;
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof ImageSelectView) {
                firstImage = false;
                break;
            }
        }
        if (firstImage) {
            view.setSelected(true);
        }
        Editable text = mCurrentEdit.getText();
        if (mPositionInEdit == 0) {
            addView(view, mPositionInParent);
            addView(new TextEditSplit(mActivity), mPositionInParent);
        } else if (mPositionInEdit >= text.length()) {
            boolean isEnd = mPositionInParent == getChildCount() - 1;
            boolean isMiddle = false;
            if (!isEnd) {
                isMiddle = getChildAt(mPositionInParent + 1) instanceof ImageSelectView;
            }
            addView(view, mPositionInParent + 1);
            if (isEnd) {
                addView(new TextEditSplit(mActivity), getChildCount());
            } else if (isMiddle) {
                addView(new TextEditSplit(mActivity), mPositionInParent + 2);
            }
        } else {
            TextEditSplit newEdit = new TextEditSplit(mActivity);
            newEdit.setText(text.subSequence(mPositionInEdit, text.length()));
            addView(view, mPositionInParent + 1);
            addView(newEdit, mPositionInParent + 2);
            mCurrentEdit.setText(text.subSequence(0, mPositionInEdit));
        }
    }

    private void removeImageView(ImageSelectView view) {
        int index = indexOfChild(view);
        if ((index > 0) && (index < getChildCount() - 1)) {
            View after = getChildAt(index + 1);
            View before = getChildAt(index - 1);
            if (after instanceof TextEditSplit && before instanceof TextEditSplit) {
                ((TextEditSplit) before).setText(((TextEditSplit) before).getText().toString()
                        + ((TextEditSplit) after).getText().toString());
                removeView(after);
            }
        }
        removeView(view);
    }

    @Override
    public void onImageSelected(ImageSelectView view) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof ImageSelectView) {
                ((ImageSelectView) child).setSelected(false);
            }
        }
        view.setSelected(true);
    }

    @Override
    public void onImageClick(final ImageSelectView imageSelectView) {
        AlertDialog.Builder builder = MessageUtils.createHoloBuilder(mActivity);
        builder.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.ct_choice_item, R.id.checktext,
                new String[]{getResources().getString(R.string.ct_delete),
                        getResources().getString(R.string.cancel)}), new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    MessageUtils.createHoloBuilder(mActivity).setMessage(R.string.ct_delete_pic_confirm)
                            .setPositiveButton(R.string.ct_delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    imageSelectView.destroy();
                                    removeImageView(imageSelectView);
                                    if (!TextUtils.isEmpty(imageSelectView.getRemoteUrl())) {
                                        mUrlsToDeleted.add(imageSelectView.getRemoteUrl());
                                    }
                                }
                            })
                            .setNegativeButton(R.string.ct_cancel, null).show();
                }
            }
        });
        builder.create().show();
    }

    public void deletePictures(){
        if(mUrlsToDeleted.isEmpty()){
            return;
        }
        LabAsyncHttpResponseHandler listener = new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
            }
        };
        for(String url :mUrlsToDeleted){
            ServiceBusiness.delPic(mActivity, mClient,listener, url);
        }
    }
}
