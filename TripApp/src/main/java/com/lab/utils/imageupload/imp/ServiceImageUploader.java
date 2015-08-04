package com.lab.utils.imageupload.imp;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.service.R;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.GetImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.imageupload.IImageUploader;
import com.lab.utils.imageupload.ImageUploadCallback;
import com.loopj.android.http.AsyncHttpClient;


/**
 * Created by baziii on 15/7/28.
 */
public class ServiceImageUploader implements IImageUploader {
    public static final String TAG = "ImageUploader";
    AsyncHttpClient mClient;
    Context mContext;

    public ServiceImageUploader( AsyncHttpClient mClient, Context context) {
        this.mClient = mClient;
        this.mContext = context;
    }

    @Override
    public void upload(final Bitmap bitmap,final ImageUploadCallback callback) {
        String base64 = GetImageHelper.bitmapToBase64String(bitmap);

        ServiceBusiness.upPic(mContext, mClient, new LabAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(LabResponse response, Object data) {
                try {
                    JSONObject json = JSONObject.parseObject(data.toString());
                    String url = json.getString("picurl");
                    if (TextUtils.isEmpty(url)) {
                        throw new Exception();
                    } else {
                        callback.onSuccess(bitmap, url);
                    }
                } catch (Exception e) {
                    LogHelper.e(TAG, "update pic: " + e);
                    //upload failed remove spannable??
                    MessageUtils.showToast(R.string.ct_upload_failed);
                    callback.onFailed(e);
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                if (!TextUtils.isEmpty(response.msg)) {
                    callback.onFailed(new Exception(response.msg));
                } else {
                    callback.onFailed(new Exception(
                            mContext.getResources().getString(R.string.ct_upload_failed)
                    ));
                }
                //upload failed ->retry or give up
            }
        }, base64);
    }
}
