package com.lab.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cuitrip.service.R;
import com.lab.utils.ImageHelper;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created on 7/25.
 */
public class ImageSelectView extends FrameLayout {

    private ImageView mImageView;
    private CheckBox mCheckBox;
    private ProgressBar mProgress;
    private OnImageClickdCallback mCallBack;

    private String mUrl;
    private Bitmap mBitmap;

    public ImageSelectView(Context context) {
        super(context);
        init();
    }

    public ImageSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.ct_image_selector, this);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mImageView = (ImageView) findViewById(R.id.image_pic);
        mProgress = (ProgressBar) findViewById(R.id.loading);
        mCheckBox = (CheckBox) findViewById(R.id.main_selected);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(mCallBack != null && checked){
                    mCallBack.onImageSelected(ImageSelectView.this);
                }
            }
        });
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mCallBack != null) {
                    mCallBack.onImageClick(ImageSelectView.this);
                }
            }
        });
    }

    public void setSelected(boolean selected){
        mCheckBox.setChecked(selected);
        if(selected){
            mCheckBox.setText(getResources().getString(R.string.ct_mainpic_setted));
        }else{
            mCheckBox.setText(getResources().getString(R.string.ct_set_main_pic));
        }
    }

    public boolean isSelected(){
        return mCheckBox.isChecked();
    }

    public void setImageSource(Drawable drawable){
        mImageView.setImageDrawable(drawable);
    }

    public void setImageBitmap(Bitmap bp){
        mBitmap = bp;
        mImageView.setImageBitmap(bp);
    }

    public interface OnImageClickdCallback {
        void onImageSelected(ImageSelectView view);
        void onImageClick(ImageSelectView view);
    }

    public void setOnImageClickdCallback(OnImageClickdCallback mCallBack) {
        this.mCallBack = mCallBack;
    }

    public ImageView getImageView(){
        return mImageView;
    }

    public void setUpdating(boolean updating){
        mProgress.setVisibility(updating ? VISIBLE : GONE);
    }

    public boolean isUpdating(){
        return mProgress.getVisibility() == VISIBLE;
    }

    public void setRemoteUrl(String url){
        mUrl = url;
        ImageHelper.displayImage(url, mImageView, null);
    }

    public String getRemoteUrl(){
        return mUrl;
    }

    public void destroy(){
        if(mBitmap != null){
            mBitmap.recycle();
        }
    }
}
