package com.lab.utils.imageupload;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;

/**
 * Created by baziii on 15/7/28.
 */
public class URLDrawable extends BitmapDrawable {
    // the drawable that you need to set, you could set the initial drawing
    // with the loading image if you need to
    protected Drawable drawable;

    protected Drawable defaultDrawable;
    public URLDrawable() {
     defaultDrawable = MainApplication.getInstance().getResources().getDrawable(R.drawable.ct_default);
    }

    @Override
    public void draw(Canvas canvas) {
        // override the draw to facilitate refresh function later
        if(drawable != null) {
            drawable.draw(canvas);
        } else {
            defaultDrawable.draw(canvas);
        }
    }
}