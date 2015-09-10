package com.lab.utils.imageupload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;
import com.lab.utils.ImageHelper;
import com.lab.utils.LogHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

/**
 * Created by baziii on 15/7/28.
 */
public class URLImageParser implements Html.ImageGetter {
    public static final String TAG = "URLImageParser";
    public static final String BAD_IMAGE = "< img src=";
    public static final String GOOD_IMAGE = "<img src=";
    public static final String BAD_WIDTH = "width=\"100\"";
    public static final String GOOD_WITDH = "width=\"100%\"";
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

    public static String replae(String oldContent) {
        return oldContent.replaceAll(BAD_IMAGE, GOOD_IMAGE);
    }

    public static String replaeWidth(String oldContent) {
        return oldContent.replaceAll(BAD_WIDTH, GOOD_WITDH);
    }

    public Drawable getDrawable(String source) {
        if (urlMap.containsKey(source)) {
            return urlMap.get(source);
        }
        Drawable urlDrawable = MainApplication.getInstance().getResources().getDrawable(R.drawable.ct_default);

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
            LogHelper.e(TAG, "w/h " + result.getIntrinsicWidth() + "/" + result.getIntrinsicHeight() + "  " + result.getBounds());
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
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(urlString, ImageHelper.getDefaultDisplayImageOptions());
//                InputStream is = fetch(urlString);
//                Drawable drawable = Drawable.createFromStream(is, "src");
                Drawable drawable = new BitmapDrawable(bitmap);
                int tempWidth = bitmap.getScaledWidth(MainApplication.getInstance().getResources().getDisplayMetrics());
                int height = bitmap.getScaledHeight(MainApplication.getInstance().getResources().getDisplayMetrics()) * 2 / 3;
//                drawable.setBounds(0,0,tempWidth,height);
                LogHelper.e("omg", "normal  " + tempWidth + " -" + height);
                LogHelper.e("omg", "page  " + MainApplication.getInstance().getPageWidth() + " -" + MainApplication.getInstance().getPageHeight());
                int width = MainApplication.getInstance().getPageWidth();
                float leftPadding = MainApplication.getInstance().getResources().getDimension(R.dimen.ct_personal_desc_left_padding);
                float topPadding = MainApplication.getInstance().getResources().getDimension(R.dimen.ct_personal_desc_top_padding);
//                height =  width * height /tempWidth ;
                drawable.setBounds((int) leftPadding, 0, (width > 0 ? width : 0) - (int) leftPadding * 2, (height > 0 ? height : 0));
                LogHelper.e("omg", "bound " + drawable.getBounds().width() + " - " + drawable.getBounds().height());
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }
    }
}