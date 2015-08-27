package com.lab.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by baziii on 15/8/27.
 */
public class MutiEditView extends LinearLayout {

    public MutiEditView(Context context) {
        super(context);
        init();
    }

    public MutiEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MutiEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public MutiEditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        setOrientation(VERTICAL);
    }

    public void buildEdit() {
        if (getChildCount() == 0) {
            EditText editText=buildEdittext();
            editText.setHint(" please input");
        }
    }

    public void insertImage(String url){
        buildImage(url);
        appendEdit().requestFocus();
    }

    public void setText(String introduce) {
        removeAllViews();
        while (!TextUtils.isEmpty(introduce) && introduce.indexOf("<div>") >= 0) {
            if (introduce.indexOf("<div>") >= 0) {
                String temp = introduce.substring(0, introduce.indexOf("<div>"));
                introduce = introduce.substring(introduce.indexOf("<div>") + 5);
                LogHelper.e("now ", introduce);
                buildTextView(temp);
                temp = introduce.substring(0, introduce.indexOf("</div>"));
                buildImage(temp);
                introduce = introduce.substring(introduce.indexOf("</div>") + 6);
            }
        }
        buildTextView(introduce);
    }
//
//    @Override
//    public void addView(View child) {
//        for (int i =  getChildCount(); i>0 ; i--) {
//            if (
//                    ( getChildAt(i) instanceof TextView )&&
//                            TextUtils.isEmpty(((TextView) getChildAt(i)).getText().toString())){
//                removeViewAt(i);
//            }
//        }
//        super.addView(child);
//    }

    public EditText buildEdittext() {
        EditText textView = (EditText) LayoutInflater.from(getContext()).inflate(R.layout.edit, this, false);
        addView(textView);
        return textView;
    }

    public EditText appendEdit(){
        EditText editText = buildEdittext();
        editText.setKeyListener(new KeyListener() {
            @Override
            public int getInputType() {
                return 0;
            }

            @Override
            public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_DEL == event.getKeyCode()) {
                    int preIndex = findPreCildIndex(view);
                        if (preIndex<=0){
                            removeViewAt(preIndex);
                            return true;
                        }
                }
                return false;
            }

            @Override
            public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable text, KeyEvent event) {
                return false;
            }

            @Override
            public void clearMetaKeyState(View view, Editable content, int states) {

            }
        });
        return  editText;
    }

    public int findPreCildIndex(View view){
        for (int i = 0; i < getChildCount(); i++) {
           if( getChildAt(i).equals(view)){
               return i;
           }
        }
        return -1;
    }

    public void buildTextView(String temp) {
        if (!TextUtils.isEmpty(temp)) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tv, this, false);
            textView.setText(temp);

            addView(textView);
        }
    }

    public void buildImage(String image) {

        try {
            image = image.substring(image.indexOf("http:"), image.indexOf("\"", image.indexOf("http:")));
            ImageView textView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.tv_image, this, false);
            addView(textView);
//            ImageHelper.displayCtImage(image, textView, null);
            startDiaplay(textView, image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDiaplay(final ImageView imageView, final String url) {
        imageView.setImageResource(R.drawable.ct_default);
        new AsyncTask() {
            Bitmap bitmap;
            float height;
            float width;

            @Override
            protected Object doInBackground(Object[] params) {
                bitmap = ImageLoader.getInstance().loadImageSync(url, ImageHelper.getDefaultDisplayImageOptions());
                int tempWidth = bitmap.getWidth();
                height = bitmap.getHeight();
                width = MainApplication.getInstance().getPageWidth();
                height = width * height / tempWidth;
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                try {
                    if (bitmap != null && imageView != null) {
                        imageView.setImageBitmap(bitmap);
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        params.width = (int) width;
                        params.height = (int) height;
                        imageView.setLayoutParams(params);
                        imageView.invalidate();
                        LogHelper.e("omg", "set image " + width + "|" + height);
                    }
                } catch (Exception e) {
                }
                super.onPostExecute(o);
            }


        }.execute(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
