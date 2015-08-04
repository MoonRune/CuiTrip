package com.lab.utils.imageupload;

import android.graphics.Bitmap;

/**
 * Created by baziii on 15/7/28.
 */
public interface IImageUploader {
    void upload(final Bitmap bitmap, final ImageUploadCallback callback);
}
