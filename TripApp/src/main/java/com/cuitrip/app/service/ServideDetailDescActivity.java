package com.cuitrip.app.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.model.ServiceInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.lab.utils.NumberUtils;
import com.lab.utils.imageupload.URLImageParser;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/17.
 */
public class ServideDetailDescActivity extends BaseActivity {
    public static final String SERVIDE_ID = "ServideDetailDescActivity";
    @InjectView(R.id.ce_service_name)
    TextView ceServiceName;
    @InjectView(R.id.ce_service_location)
    TextView ceServiceLocation;
    @InjectView(R.id.service_score)
    RatingBar serviceScore;
    @InjectView(R.id.ct_content_v)
    TextView ctContentV;
    @InjectView(R.id.ct_book)
    Button ctBook;
    ServiceInfo serviceInfo;
    @InjectView(R.id.content)
    LinearLayout content;

    public static void start(Context context, ServiceInfo serviceInfo) {

        context.startActivity(new Intent(context, ServideDetailDescActivity.class)
                .putExtra(SERVIDE_ID, serviceInfo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_service_desc_detail);
        ButterKnife.inject(this);
        if (getIntent().hasExtra(SERVIDE_ID) && getIntent().getSerializableExtra(SERVIDE_ID) instanceof ServiceInfo) {
            serviceInfo = (ServiceInfo) getIntent().getSerializableExtra(SERVIDE_ID);
        } else {
            MessageUtils.showToast(R.string.data_error);
            finish();
            return;
        }
        showActionBar(getString(R.string.ct_detail_desc));
        ceServiceName.setText(serviceInfo.getName());
        ceServiceLocation.setText(serviceInfo.getAddress());
        serviceScore.setRating(NumberUtils.paserFloat(serviceInfo.getScore()));
        String introduce = URLImageParser.replae(serviceInfo.getDescpt());
        introduce = URLImageParser.replaeWidth(introduce);
//        URLImageParser p = new URLImageParser(ctContentV, ServideDetailDescActivity.this, introduce);
//        ctContentV.setText(Html.fromHtml(introduce, p, null));
//        ctBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CreateOrderActivity.start(ServideDetailDescActivity.this, serviceInfo);
//            }
//        });
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

    public void buildTextView(String temp) {

        if (!TextUtils.isEmpty(temp)) {
            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.tv, content, false);
            textView.setText(temp);

            content.addView(textView);
        }
    }

    public void buildImage(String image) {

        try {
            image = image.substring(image.indexOf("http:"), image.indexOf("\"", image.indexOf("http:")));
            ImageView textView = (ImageView) LayoutInflater.from(this).inflate(R.layout.tv_image, content, false);
            content.addView(textView);
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
                    if (bitmap != null&&imageView!=null) {
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
