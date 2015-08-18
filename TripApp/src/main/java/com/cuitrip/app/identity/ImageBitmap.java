package com.cuitrip.app.identity;

import android.graphics.Bitmap;

/**
 * Created by baziii on 15/8/18.
 */
public class ImageBitmap {
    private Bitmap bitmap;
    private String url;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
