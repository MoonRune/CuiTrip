package com.lab.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lab.utils.Constants;
import com.lab.utils.LogHelper;
import com.lab.utils.imageupload.URLImageParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 7/14.
 */
public class BrowserActivity extends BaseActivity {
    public static final String TITLE = "title";
    public static final String IS_DATA = "IS_DATA";
    public static final String DATA = "BROWSER_DATA";

    public static final Pattern ACCEPTED_URI_SCHEMA = Pattern.compile(
            "(?i)" + // switch on case insensitive matching
                    "(" +    // begin group for schema
                    "(?:http|https|file):\\/\\/" +
                    "|(?:inline|data|about|javascript):" +
                    "|(?:.*:.*@)" +
                    ")" +
                    "(.*)" );
    /*
     * The WebView that is placed in this Activity
     */
    protected WebView mWebView;
    private String mTitle;

    /*
     * As the file content is loaded completely into RAM first, set a limitation
     * on the file size so we don't use too much RAM. If someone wants to load
     * content that is larger than this, then a content provider should be used.
     */
    static final int MAXFILESIZE = 8096;

    static final String LOGTAG = "HTMLViewerActivity";

    public static void startWithData(Context context,String data){
        Intent intent =new Intent(context,BrowserActivity.class);
        intent.putExtra(IS_DATA,true);
        intent.putExtra(DATA,data);
        context.startActivity(intent);
    }
    public class JumpConcat{
        @JavascriptInterface
        public void jumpConcat(){
           LogHelper.e("omg","asdfasdfasfsadf");
        }

    }
    public void buildWebView(){

        mWebView = new WebView(this);
        setContentView(mWebView);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);

        // Call createInstance() explicitly. createInstance() is called in
        // BrowserFrame by WebView. As it is called in WebCore thread, it can
        // happen after onResume() is called. To use getInstance() in onResume,
        // createInstance() needs to be called first.
        CookieSyncManager.createInstance(this);
        buildWebView();

        // Setup callback support for title and progress bar
        mWebView.setWebChromeClient(new WebChrome());
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.addJavascriptInterface(new JumpConcat(),"jumper");
        mWebView.requestFocus();
        // Configure the webview
        WebSettings s = mWebView.getSettings();
        s.setUseWideViewPort(true);
        s.setSupportZoom(true);
        //s.setBuiltInZoomControls(true);
        s.setSaveFormData(false);
        s.setBlockNetworkLoads(false);
        s.setDefaultTextEncodingName("utf-8");

        // Javascript is purposely disabled, so that nothing can be
        // automatically run.
        s.setJavaScriptEnabled(true);


        LogHelper.e("omg"," data"+getIntent().getStringExtra(DATA));
        // Restore a webview if we are meant to restore
        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            // Check the intent for the content to view
            Intent intent = getIntent();
            if(intent != null){
                boolean isData = intent.getBooleanExtra(IS_DATA, false);
                String data = intent.getStringExtra(DATA);
                data= URLImageParser.replae(data);
                data=URLImageParser.replaeWidth(data);
                if(isData){
                    String url = "";
                    Pattern pattern = Pattern.compile(Constants.ONLY_IMAGE_PATTERN);
                    Matcher matcher = pattern.matcher(data);
                    int index = 0;
                    if (matcher.find()) {
                        int start = matcher.start();
                        String temp = data.substring(index, start);
                        LogHelper.e("omg"," temp:"+temp);
                        try {
                            int position = temp.indexOf("/",8);
                            if (position>0){
                                url= temp.substring(0,position);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    LogHelper.e("omg"," url:"+url);
                    mWebView.loadDataWithBaseURL(url, data, "text/html", "utf-8", null);
                }else{
                    mWebView.loadUrl(data);
                }
            }
            mTitle = intent.getStringExtra(TITLE);
            showActionBar(mTitle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

        @Override
    protected void onSaveInstanceState(Bundle outState) {
        // the default implementation requires each view to have an id. As the
        // browser handles the state itself and it doesn't use id for the views,
        // don't call the default implementation. Otherwise it will trigger the
        // warning like this, "couldn't save which view has focus because the
        // focused view
        mWebView.saveState(outState);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mWebView != null && mWebView.canGoBack()){
                mWebView.goBack();
                return true;
            }else{
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();

        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    class WebChrome extends WebChromeClient {


        @Override
        public void onReceivedTitle(WebView view, String title) {
            if(TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(title)){
                showActionBar(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            getWindow().setFeatureInt(
                    Window.FEATURE_PROGRESS, newProgress * 100);
            if (newProgress == 100) {
                CookieSyncManager.getInstance().sync();
            }
        }
    }

    private final WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogHelper.e("omg","url"+url);
            if(ACCEPTED_URI_SCHEMA.matcher(url).matches()){
                return false;
            }
            return true;
        }
    };
}
