package com.lab.utils.imageupload;

import android.graphics.Bitmap;

/**
 * Created by baziii on 15/7/28.
 */
public interface ImageUploadCallback {
    void onSuccess(Bitmap bitmap, String url);
    void onFailed(Throwable throwable);
}
