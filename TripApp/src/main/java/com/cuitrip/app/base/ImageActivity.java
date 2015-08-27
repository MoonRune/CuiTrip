package com.cuitrip.app.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by baziii on 15/8/26.
 */
public class ImageActivity extends BaseActivity {
    public static final String TAG = "ImageActivity";
    public static final String KEY = "ImageActivity.KEY";

    public static void start(Context context, Uri uri) {
        context.startActivity(new Intent(context, ImageActivity.class).putExtra(KEY, uri));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(R.string.rc_plugins_image);
        setContentView(R.layout.image_v);
        Uri uri = (getIntent().getParcelableExtra(KEY));
        LogHelper.e(TAG, uri.buildUpon().toString());
        ImageHelper.displayImage(uri.buildUpon().toString(), ((ImageView) findViewById(R.id.image)),
                ImageHelper.getCtDisplayImageOptions(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        showNoCancelDialog();
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        hideNoCancelDialog();
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        hideNoCancelDialog();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        hideNoCancelDialog();
                    }
                });
    }
}
