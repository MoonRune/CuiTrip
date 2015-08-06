package com.lab.utils.imageupload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;
import com.lab.utils.LogHelper;
import com.loopj.android.http.HttpGet;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Created by baziii on 15/7/28.
 */
public class URLImageParser implements Html.ImageGetter {
    public static final String TAG = "URLImageParser";
    public static final String BAD_IMAGE="< img src=";
    public static final String GOOD_IMAGE="<img src=";
    public static final String BAD_WIDTH="width=\"100\"";
    public static final String GOOD_WITDH="width=\"100%\"";
    Context c;
    TextView container;
    public HashMap<String, Drawable> urlMap = new HashMap<>();
    String html;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     *
     * @param t
     * @param c
     */
    public URLImageParser(TextView t, Context c, String html) {
        this.c = c;
        this.container = t;
        this.html = replae(html);
    }

    public static String replae(String oldContent){
        return oldContent.replaceAll(BAD_IMAGE,GOOD_IMAGE);
    }
    public static String replaeWidth(String oldContent){
        return oldContent.replaceAll(BAD_WIDTH,GOOD_WITDH);
    }


    public static String badReplae(String oldContent){
        return oldContent.replaceAll(GOOD_IMAGE,BAD_IMAGE);
    }

    public Drawable getDrawable(String source) {
        if (urlMap.containsKey(source)) {
            return urlMap.get(source);
        }
        Drawable urlDrawable = MainApplication.sContext.getResources().getDrawable(R.drawable.ct_default);

        // get the actual source
        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask();

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag

        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        String source;

        public ImageGetterAsyncTask() {
        }

        @Override
        protected Drawable doInBackground(String... params) {
            source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // set the correct bound according to the result from HTTP call
            LogHelper.e(TAG, "w/h " + result.getIntrinsicWidth() + "/" + result.getIntrinsicHeight()+"  "+result.getBounds());
            // change the reference of the current drawable to the result
            // from the HTTP call
            urlMap.put(source, result);

            // redraw the image by invalidating the container
            container.setText(Html.fromHtml(html, URLImageParser.this, null));
        }

        /***
         * Get the Drawable from URL
         *
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                int tempWidth = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                int width = MainApplication.sContext.getPageWidth();
                float leftPadding = MainApplication.sContext.getResources().getDimension(R.dimen.ct_personal_desc_left_padding);
                float topPadding = MainApplication.sContext.getResources().getDimension(R.dimen.ct_personal_desc_top_padding);
                width -= leftPadding;
                height = width * height / tempWidth;
                drawable.setBounds((int) leftPadding, (int) topPadding, (width > 0 ? width : 0) - (int) leftPadding * 2, (height > 0 ? height : 0) + (int) topPadding );
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);
            HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
        }
    }
}